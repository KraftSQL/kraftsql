package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import kotlin.reflect.KClass

/**
 * Generic [SimulatorConnection], that implements common behavior of SQL engines and dialects for testing.
 *
 * The [GenericSimulatorConnection] is configurable and extendable to simulate specific SQL engines and dialects.
 *
 * @param <E> the [Engine] to simulate
 */
open class GenericSimulatorConnection<E : Engine<E>>(
    private val orm: SimulatorORMapping<E> = SimulatorORMapping(),
    protected val rootState: EngineState<E> = EngineState(),
) : SimulatorConnection<E> {
    protected var topState:  EngineState<E> = rootState

    protected open class TransactionStateOverlay<E : Engine<E>>(
        val parent: EngineState<E>,
    ) : EngineState<E>() {
        override fun containsTable(name: String): Boolean {
            return super.containsTable(name) || parent.containsTable(name)
        }

        override fun findTable(name: String): Pair<Table<E, *>, MutableList<DataRow>>? {
            return super.findTable(name) ?: parent.findTable(name)
        }

        fun ensureTableCopy(name: String) {
            tables.computeIfAbsent(name) { getTable(name).run { first to second.toMutableList() } }
        }

        fun commitIntoParent(): EngineState<E> {
            tables.values.forEach { (table, data) -> parent.writeTable(table, data) }
            return parent
        }
    }

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
        check(!rootState.containsTable(createTable.table.qualifiedName)) { "Table '${createTable.table.qualifiedName}' already exists." }
        rootState.addTable(createTable.table)
    }

    override fun execute(dropTable: DropTable<E>) {
        commitAllOpenTransactions()
        if (!dropTable.ifExists) {
            check(rootState.containsTable(dropTable.table.qualifiedName)) { "Table '${dropTable.table.qualifiedName}' does not exist." }
        }
        rootState.removeTable(dropTable.table)
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        val table = topState
            .also { (it as? TransactionStateOverlay<E>)?.ensureTableCopy(insertInto.table.qualifiedName) }
            .getTable(insertInto.table.qualifiedName)
        val rows = insertInto.values.let { values ->
            when (values) {
                is ConstantData -> values.items.map { item -> simulateExpression(orm.serialize(item)).invoke(DataRow(emptyMap())) as DataRow }
                else -> throw NotImplementedError("Inserting ${values::class.qualifiedName} is not implemented.")
            }
        }
        rows.forEach { row ->
            require(row.values.keys == table.first.columns.map { column -> column.name }.toSet()) {
                "$row to insert doesn't match table schema of '${table.first.qualifiedName}'."
            }
            require(table.first.columns.all { column -> column.nullable || row.values[column.name] != null }) {
                "$row to insert violates NOT NULL constraint of a column in '${table.first.qualifiedName}'."
            }
            table.second.add(row)
        }
        return rows.count()
    }

    override fun execute(delete: Delete<E>): Int {
        val table = topState
            .also { (it as? TransactionStateOverlay<E>)?.ensureTableCopy(delete.table.qualifiedName) }
            .getTable(delete.table.qualifiedName)
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
        topState = TransactionStateOverlay(topState)
    }

    override fun execute(commitTransaction: CommitTransaction<E>) {
        topState = topState.let {
            if (it is TransactionStateOverlay<E>) {
                it.commitIntoParent()
            } else {
                throw IllegalStateException("No open transaction to commit.")
            }
        }
    }

    private fun commitAllOpenTransactions() {
        while (topState is TransactionStateOverlay<E>) execute(CommitTransaction())
    }

    override fun execute(rollbackTransaction: RollbackTransaction<E>) {
        topState = topState.let {
            if (it is TransactionStateOverlay<E>) {
                it.parent
            } else {
                throw IllegalStateException("No open transaction to roll back.")
            }
        }
    }

    protected open fun fetchData(source: QuerySource<E, *>) : List<DataRow> {
        var rows = source.data.let { data -> when (data) {
            is Table<E, *> -> topState.getTable(data.qualifiedName).second
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

    private val expressionSimulators: MutableMap<KClass<*>, ExpressionSimulator<E, *, *>> = mutableMapOf()

    fun <T, X: Expression<E, T>> registerExpressionSimulator(expressionSimulator: ExpressionSimulator<E, T, X>) {
        expressionSimulators[expressionSimulator.expression] = expressionSimulator
    }

    fun unregisterExpressionSimulator(expression: KClass<out Expression<*, *>>) {
        expressionSimulators.remove(expression)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, X: Expression<E, T>> getExpressionSimulator(expression: X) =
        expressionSimulators.getOrElse(expression::class) {
            throw NotImplementedError("Simulation of a ${expression::class.qualifiedName} is not implemented.")
        } as ExpressionSimulator<E, T, X>

    init {
        registerExpressionSimulator(ConstantSimulator())
        registerExpressionSimulator(ColumnSimulator())
        registerExpressionSimulator(CastSimulator())
        registerExpressionSimulator(IsNotNullSimulator())
        registerExpressionSimulator(EqualsSimulator())
        registerExpressionSimulator(ArraySimulator<E, Any>())
        registerExpressionSimulator(RowSimulator())
        registerExpressionSimulator(CountSimulator())
        registerExpressionSimulator(SumAsLongSimulator())
        registerExpressionSimulator(SumAsDoubleSimulator())
        registerExpressionSimulator(SumAsBigDecimalSimulator())
    }

    protected open fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T? {
        context(
            object : ExpressionSimulator.SubexpressionCallbacks<E> {
                override fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T = { row ->
                    context(this) { getExpressionSimulator(expression).simulateExpression(expression)(row) }
                }

                context(groupExpressions: List<Expression<E, *>>)
                override fun <T> simulateAggregation(expression: Expression<E, T>) =
                    throw IllegalStateException("sub-expression cannot be an aggregation")
            }
        ) {
            return getExpressionSimulator(expression).simulateExpression(expression)
        }
    }

    protected open fun <T> simulateAggregation(expression: Expression<E, T>, groupExpressions: List<Expression<E, *>>): (List<DataRow>) -> T? {
        context(
            groupExpressions,
            object : ExpressionSimulator.SubexpressionCallbacks<E> {
                override fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T = { row ->
                    context(this) { getExpressionSimulator(expression).simulateExpression(expression)(row) }
                }

                context(groupExpressions: List<Expression<E, *>>)
                override fun <T> simulateAggregation(expression: Expression<E, T>): (List<DataRow>) -> T = { rows ->
                    context(groupExpressions, this) {
                        if (expression in groupExpressions) {
                            getExpressionSimulator(expression).simulateExpression(expression)(rows.first())
                        } else {
                            getExpressionSimulator(expression).simulateAggregation(expression)(rows)
                        }
                    }
                }
            }
        ) {
            return if (expression in groupExpressions) {
                { rows -> getExpressionSimulator(expression).simulateExpression(expression)(rows.first()) }
            } else {
                getExpressionSimulator(expression).simulateAggregation(expression)
            }
        }
    }
}
