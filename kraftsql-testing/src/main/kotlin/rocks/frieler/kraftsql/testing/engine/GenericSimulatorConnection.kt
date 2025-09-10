package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.SumAsDouble
import rocks.frieler.kraftsql.expressions.SumAsLong
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.expressions.SumAsBigDecimal
import java.math.BigDecimal
import java.sql.SQLSyntaxErrorException
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

open class GenericSimulatorConnection<E : Engine<E>>(
    private val orm: SimulatorORMapping<E> = SimulatorORMapping()
) : SimulatorConnection<E> {
    private val tables: MutableMap<String, Pair<Table<E, *>, MutableList<DataRow>>> = mutableMapOf()
    private val transactionsOverlayData = mutableListOf<MutableMap<String, Pair<Table<E, *>, MutableList<DataRow>>>>()

    override fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T> {
        var rows = fetchData(select.source)

        for (join in select.joins) {
            val dataToJoin = fetchData(join.data)
            val joinCondition = simulateExpression(join.condition)
            when (join) {
                is InnerJoin<E> -> {
                    rows = rows.flatMap { row ->
                        dataToJoin
                            .map { rowToJoin -> row + rowToJoin }
                            .filter { row -> joinCondition.invoke(row) ?: false }
                    }
                }
                else -> NotImplementedError("Simulation of ${join::class.qualifiedName} is not implemented.")
            }
        }

        select.filter?.let { filter ->
            val filterCondition = simulateExpression(filter)
            rows = rows.filter { row -> filterCondition.invoke(row) ?: false }
        }

        if (select.grouping.isNotEmpty()) {
            val groupingExtractors = select.grouping.map { expression -> simulateExpression(expression) }
            val rowGroups = rows.groupBy { row -> groupingExtractors.map { it.invoke(row) } }.values

            val projections = (select.columns ?: select.grouping.map { Projection<E, _>(it) })
                .associate { (it.alias ?: it.value.defaultColumnName()) to simulateAggregation(it.value, select.grouping) }
            rows = rowGroups.map { rowGroup ->
                DataRow(projections.mapValues { (_, expression) -> expression.invoke(rowGroup) })
            }
        } else {
            val projections = (
                    select.columns
                    ?: (select.source.data as? Table)?.columns?.map { Projection(select.source[it.name]) }
                    ?: throw NotImplementedError("Simulation of 'SELECT *' is not implemented.")
                ).associate { (it.alias ?: it.value.defaultColumnName()) to simulateExpression(it.value) }
            rows = rows.map { row ->
                DataRow(projections.mapValues { (_, expression) -> expression.invoke(row) })
            }
        }

        return orm.deserializeQueryResult(rows, type)
    }

    override fun execute(createTable: CreateTable<E>) {
        commitAllOpenTransactions()
        check(createTable.table.qualifiedName !in tables) { "Table '${createTable.table.qualifiedName}' already exists." }
        tables[createTable.table.qualifiedName] = createTable.table to mutableListOf()
    }

    override fun execute(dropTable: DropTable<E>) {
        commitAllOpenTransactions()
        if (!dropTable.ifExists) {
            check(dropTable.table.qualifiedName in tables) { "Table '${dropTable.table.qualifiedName}' does not exist." }
        }
        tables.remove(dropTable.table.qualifiedName)
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        val table = ensureTableCopyInTransactionIfOpen(insertInto.table.qualifiedName)
        val rows = insertInto.values.let { values ->
            when (values) {
                is ConstantData -> values.items.map { item -> simulateExpression(orm.serialize(item)).invoke(DataRow(emptyMap())) as DataRow }
                else -> throw NotImplementedError("Inserting ${values::class.qualifiedName} is not implemented.")
            }
        }
        rows.forEach { row ->
            if (row.values.keys != table.first.columns.map { column -> column.name }.toSet()) {
                throw IllegalArgumentException("$row to insert doesn't match table schema of '${table.first.qualifiedName}'.")
            }
            table.second.add(row)
        }
        return rows.count()
    }

    override fun execute(delete: Delete<E>): Int {
        val table = ensureTableCopyInTransactionIfOpen(delete.table.qualifiedName)
        val tableSizeBefore = table.second.size
        val condition = delete.condition?.let { simulateExpression(it) }
        if (condition == null) {
            table.second.clear()
        } else {
            table.second.removeAll { row -> condition.invoke(row) ?: false }
        }
        return tableSizeBefore - table.second.size
    }

    override fun execute(beginTransaction: BeginTransaction<E>) {
        transactionsOverlayData.add(mutableMapOf())
    }

    private fun ensureTableCopyInTransactionIfOpen(qualifiedName: String) =
        if (transactionsOverlayData.isNotEmpty()) {
            transactionsOverlayData.last().computeIfAbsent(qualifiedName) { findTable(qualifiedName).run { first to second.toMutableList() } }
        } else {
            findTable(qualifiedName)
        }

    override fun execute(commitTransaction: CommitTransaction<E>) {
        val dataToCommit = transactionsOverlayData.removeLast()
        val stateToCommitInto = if (transactionsOverlayData.isNotEmpty()) transactionsOverlayData.last() else tables
        dataToCommit.forEach { (qualifiedName, data) ->
            stateToCommitInto[qualifiedName] = data.first to data.second
        }
    }

    private fun commitAllOpenTransactions() {
        while (transactionsOverlayData.isNotEmpty()) execute(CommitTransaction())
    }

    override fun execute(rollbackTransaction: RollbackTransaction<E>) {
        transactionsOverlayData.removeLast()
    }

    private fun findTable(name: String): Pair<Table<E, *>, MutableList<DataRow>> {
        for (state in transactionsOverlayData.reversed()) {
            state[name]?.let { return it }
        }
        return tables[name] ?: throw IllegalStateException("Table '$name' does not exist.")
    }

    protected open fun fetchData(source: QuerySource<E, *>) : List<DataRow> {
        var rows = source.data.let { data -> when (data) {
            is Table<E, *> -> findTable(data.qualifiedName).second
            is Select<E, *> -> {
                @Suppress("UNCHECKED_CAST")
                execute(data as Select<E, DataRow>, DataRow::class)
            }
            is ConstantData<E, *> -> {
                data.items.map { item ->
                    val expression = orm.serialize(item)
                    val value = simulateExpression(expression).invoke(DataRow(emptyMap()))
                    value as? DataRow ?: DataRow(mapOf(expression.defaultColumnName() to value))
                }
            }
            else -> throw NotImplementedError("Fetching ${data::class.qualifiedName} is not implemented.")
        }}

        if (source.alias != null) {
            rows = rows.map { row ->
                DataRow(row.values.mapKeys { (field, _) -> "${source.alias}.$field" })
            }
        }

        return rows
    }

    protected open fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T? {
        return when (expression) {
            is Constant<E, T> -> { _ ->
                expression.value
            }
            is Column<E, T> -> { row ->
                @Suppress("UNCHECKED_CAST")
                row[expression.qualifiedName] as T
            }
            is Cast<E, T> -> { row ->
                val targetType = expression.type.naturalKType()
                val value = simulateExpression(expression.expression).invoke(row)
                @Suppress("UNCHECKED_CAST")
                when (targetType) {
                    typeOf<Boolean>() -> value?.toString()?.toBooleanStrictOrNull()
                    typeOf<Int>() -> value?.toString()?.toInt()
                    typeOf<Long>() -> value?.toString()?.toLong()
                    typeOf<String>() -> value?.toString()
                    typeOf<LocalDate>() -> value?.toString()?.let { LocalDate.parse(it) }
                    // TODO: add support for other types as needed
                    else -> targetType.jvmErasure.cast(value)
                } as T
            }
            is Equals<E> -> { row : DataRow ->
                @Suppress("UNCHECKED_CAST") // because T must be Boolean in case of Equals
                (simulateExpression(expression.left).invoke(row) == simulateExpression(expression.right).invoke(row)) as T
            }
            is Array<E, *> -> { row ->
                @Suppress("UNCHECKED_CAST")
                if (expression.elements == null) {
                    null
                } else {
                    val elements = expression.elements!!.map { simulateExpression(it).invoke(row) }
                    val commonSuperType = elements.filterNotNull()
                        .map { setOf(it::class) + it::class.allSuperclasses }
                        .run { reduceOrNull { classes1, classes2 -> classes1.intersect(classes2) } ?: emptySet() }
                        .let { candidates -> candidates.filter { candidate -> !candidates.all { other -> other != candidate && other.isSubclassOf(candidate) } } }
                        .firstOrNull() ?: Any::class
                    java.lang.reflect.Array.newInstance(commonSuperType.java, elements.size).also { array ->
                        elements.forEachIndexed { index, element -> (array as kotlin.Array<Any?>)[index] = element }
                    }
                } as T
            }
            is Row<E, *> -> { row ->
                @Suppress("UNCHECKED_CAST")
                if (expression.values == null) {
                    null
                } else {
                    DataRow(expression.values!!.mapValues { (_, value) -> simulateExpression(value).invoke(row) })
                } as T
            }
            else -> throw NotImplementedError("Simulation of a ${expression::class.qualifiedName} is not implemented.")
        }
    }

    protected open fun <T> simulateAggregation(expression: Expression<E, T>, groupExpressions: List<Expression<E, *>>): (List<DataRow>) -> T? {
        return if (groupExpressions.contains(expression)) {
            { rows -> simulateExpression(expression).invoke(rows.first()) }
        } else when (expression) {
            is Constant<E, T> -> { _ ->
                expression.value
            }
            is Column<E, T> -> throw SQLSyntaxErrorException("'${expression.sql()}' is neither in the GROUP BY list nor wrapped in an aggregation.")
            is Equals<E> -> { rows ->
                @Suppress("UNCHECKED_CAST")
                (
                    simulateAggregation(expression.left, groupExpressions).invoke(rows)
                        == simulateAggregation(expression.right, groupExpressions).invoke(rows)
                ) as T
            }
            is Count<E> -> { rows ->
                @Suppress("UNCHECKED_CAST")
                rows.count().toLong() as T
            }
            is SumAsLong<E> -> { rows : List<DataRow> ->
                @Suppress("UNCHECKED_CAST")
                rows.map { row -> simulateExpression(expression.expression).invoke(row) as Number }.reduceOrNull { a, b -> a.toLong() + b.toLong() } as T
            }
            is SumAsDouble<E> -> { rows : List<DataRow> ->
                @Suppress("UNCHECKED_CAST")
                rows.map { row -> simulateExpression(expression.expression).invoke(row) as Number }.reduceOrNull { a, b -> a.toDouble() + b.toDouble() } as T
            }
            is SumAsBigDecimal<E> -> { rows: List<DataRow> ->
                @Suppress("UNCHECKED_CAST")
                rows.map { row -> simulateExpression(expression.expression).invoke(row) as BigDecimal }.reduceOrNull(BigDecimal::plus) as T
            }
            else -> throw NotImplementedError("Simulation of a ${expression::class.qualifiedName} is not implemented.")
        }
    }
}
