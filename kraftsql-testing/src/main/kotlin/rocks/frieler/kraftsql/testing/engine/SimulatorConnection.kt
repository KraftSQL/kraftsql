package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.Connection
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
import rocks.frieler.kraftsql.expressions.Row
import java.sql.SQLSyntaxErrorException
import kotlin.reflect.KClass

open class SimulatorConnection<E : Engine<E>>(
    private val orm: SimulatorORMapping<E> = SimulatorORMapping()
) : Connection<E> {
    private val tables: MutableMap<String, Pair<Table<E, *>, MutableList<DataRow>>> = mutableMapOf()

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
            val projections = (select.columns ?: throw NotImplementedError("Simulation of 'SELECT *' is not implemented."))
                .associate { (it.alias ?: it.value.defaultColumnName()) to simulateExpression(it.value) }
            rows = rows.map { row ->
                DataRow(projections.mapValues { (_, expression) -> expression.invoke(row) })
            }
        }

        return orm.deserializeQueryResult(rows, type)
    }

    override fun execute(createTable: CreateTable<E>) {
        check(createTable.table.qualifiedName !in tables) { "Table '${createTable.table.qualifiedName}' already exists." }
        tables[createTable.table.qualifiedName] = createTable.table to mutableListOf()
    }

    override fun execute(dropTable: DropTable<E>) {
        if (!dropTable.ifExists) {
            check(dropTable.table.qualifiedName in tables) { "Table '${dropTable.table.qualifiedName}' does not exist." }
        }
        tables.remove(dropTable.table.qualifiedName)
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        val table = tables[insertInto.table.qualifiedName] ?: throw IllegalStateException("Table '${insertInto.table.qualifiedName}' does not exist.")
        val rows = insertInto.values.let { values ->
            when (values) {
                is ConstantData -> values.items.map { item -> simulateExpression(orm.serialize(item)).invoke(DataRow(emptyMap())) as DataRow }
                else -> throw NotImplementedError("Inserting ${values::class.qualifiedName} is not implemented.")
            }
        }
        table.second.addAll(rows)
        return rows.count()
    }

    protected open fun fetchData(source: QuerySource<E, *>) : List<DataRow> {
        var rows = source.data.let { data -> when (data) {
            is Table<E, *> -> (tables[data.qualifiedName] ?: throw IllegalStateException("Table '${data.qualifiedName}' does not exist.")).second
            is Select<E, *> -> {
                @Suppress("UNCHECKED_CAST")
                execute(data as Select<E, DataRow>, DataRow::class)
            }
            is ConstantData<E, *> -> {
                data.items.map { item -> simulateExpression(orm.serialize(item)).invoke(DataRow(emptyMap())) as DataRow }
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
            is Equals<E> -> { row : DataRow ->
                @Suppress("UNCHECKED_CAST") // because T must be Boolean in case of Equals
                (simulateExpression(expression.left).invoke(row) == simulateExpression(expression.right).invoke(row)) as T
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
                rows.count() as T
            }
            is SumAsLong<E> -> { rows : List<DataRow> ->
                @Suppress("UNCHECKED_CAST")
                rows.map { row -> simulateExpression(expression.expression).invoke(row) as Number }.reduceOrNull { a, b -> a.toLong() + b.toLong() } as T
            }
            is SumAsDouble<E> -> { rows : List<DataRow> ->
                @Suppress("UNCHECKED_CAST")
                rows.map { row -> simulateExpression(expression.expression).invoke(row) as Number }.reduceOrNull { a, b -> a.toDouble() + b.toDouble() } as T
            }
            else -> throw NotImplementedError("Simulation of a ${expression::class.qualifiedName} is not implemented.")
        }
    }
}
