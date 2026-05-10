package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.dql.CrossJoin
import rocks.frieler.kraftsql.dql.DataExpressionData
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Join
import rocks.frieler.kraftsql.dql.LeftJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.QuerySource.Companion.Alias
import rocks.frieler.kraftsql.dql.RightJoin
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Aggregation
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericSubexpressionCollector
import rocks.frieler.kraftsql.testing.simulator.expressions.SubexpressionCollector
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.ifEmpty
import kotlin.collections.map
import kotlin.collections.orEmpty

/**
 * Generic, configurable evaluator that simulates SQL queries as supported by the [Engine].
 *
 * @param E the [Engine] to simulate
 * @param orm the [SimulatorORMapping] to deal with [ConstantData]
 * @param subexpressionCollector the [SubexpressionCollector] to analyze [rocks.frieler.kraftsql.expressions.Expression] trees
 * @param expressionEvaluator the [GenericExpressionEvaluator] to evaluate [rocks.frieler.kraftsql.expressions.Expression]s
 */
open class GenericQueryEvaluator<E : Engine<E>>(
    protected val orm: SimulatorORMapping<E> = SimulatorORMapping(),
    protected val subexpressionCollector: SubexpressionCollector<E> = GenericSubexpressionCollector(),
    protected val expressionEvaluator: GenericExpressionEvaluator<E> = GenericExpressionEvaluator(),
) {
    /**
     * Evaluates the given [Select] statement.
     *
     * @param select the [Select] statement to evaluate
     * @param correlatedData an optional [DataRow], that this [Select] is correlated to
     * @param activeState the current top-level [EngineState] to select from
     * @return the selected [DataRow]s
     */
    context(activeState: EngineState<E>)
    open fun selectRows(select: Select<E, *>, correlatedData: DataRow? = null) : List<DataRow> {
        var data = resolve(select.source, correlatedData)

        if (correlatedData != null) {
            data = ConstantData(orm, data.items.map { row -> correlatedData + row })
        }

        for (join in select.joins) {
            data = handleJoin(data, join)
        }

        select.filter?.let { filter ->
            val filterCondition = expressionEvaluator.simulateExpression(filter)
            data = data.items
                .filter { row -> filterCondition.invoke(row) ?: false }
                .let { rows -> if (rows.isNotEmpty()) ConstantData(orm, rows) else ConstantData.empty(orm, data.columnNames) }
        }

        val resultRows = if (select.grouping.isNotEmpty()) {
            val groupingExtractors = select.grouping.map { expression -> expressionEvaluator.simulateExpression(expression) }
            val rowGroups = data.items.groupBy { row -> groupingExtractors.map { it.invoke(row) } }.values

            val projections = (select.columns ?: select.grouping.map { Projection(it) })
                .associate { (it.alias ?: it.value.defaultColumnName()) to expressionEvaluator.simulateAggregation(it.value, select.grouping) }
            rowGroups.map { rowGroup ->
                DataRow(projections.map { (name, expression) -> name to expression.invoke(rowGroup) })
            }
        } else if (select.columns != null) {
            if (select.columns!!.all { it.value.isAggregating(true) } && select.columns!!.any { it.value.isAggregating(false) }) {
                val projections = select.columns!!
                    .associate { (it.alias ?: it.value.defaultColumnName()) to expressionEvaluator.simulateAggregation(it.value, emptyList()) }
                listOf(DataRow(projections.map { (name, expression) -> name to expression.invoke(data.items.toList()) }))
            } else {
                val projections = select.columns!!
                    .associate { (it.alias ?: it.value.defaultColumnName()) to expressionEvaluator.simulateExpression(it.value) }
                data.items.map { row ->
                    DataRow(projections.map { (name, expression) -> name to expression.invoke(row) })
                }
            }
        } else {
            data.items.toList()
        }

        return resultRows
    }

    context(activeState: EngineState<E>)
    protected open fun fetchRows(data: Data<E, *>, correlatedData: DataRow?): List<DataRow> = when (data) {
        is Table<E, *> -> activeState.getTable(data.qualifiedName).second
        is Select<E, *> -> @Suppress("UNCHECKED_CAST") selectRows(data as Select<E, DataRow>, correlatedData)
        is ConstantData<E, *> -> {
            data.items.map { item ->
                val expression = orm.serialize(item)
                val value = expressionEvaluator.simulateExpression(expression).invoke(DataRow())
                value as? DataRow ?: DataRow("" to value)
            }
        }
        is DataExpressionData<E, *> -> {
            resolve(expressionEvaluator.simulateExpression(data.expression).invoke(correlatedData ?: DataRow())).items.toList()
        }
        else -> throw NotImplementedError("Fetching ${data::class.qualifiedName} is not implemented.")
    }

    context(activeState: EngineState<E>)
    private fun resolve(data: Data<E, *>, correlatedData: DataRow? = null) : ConstantData<E, DataRow> {
        val rows = fetchRows(data, correlatedData)
        return if (rows.isNotEmpty()) ConstantData(orm, rows) else ConstantData.empty(orm, data.columnNames)
    }

    context(activeState: EngineState<E>)
    private fun resolve(querySource: QuerySource<E, *>, correlatedData: DataRow? = null) : ConstantData<E, DataRow> =
        resolve(querySource.data, correlatedData)
            .let { querySource.alias?.qualify(it) ?: it }

    private fun Alias.qualify(data: ConstantData<E, DataRow>) =
        if (data.items.none()) {
            ConstantData.empty(orm, data.columnNames.map { qualify(it) })
        } else {
            ConstantData(orm, data.items.map { row ->
                DataRow(row.entries.map { (field, value) -> qualify(field) to value })
            })
        }

    context(activeState: EngineState<E>)
    private fun handleJoin(leftSide: ConstantData<E, DataRow>, join: Join<E>): ConstantData<E, DataRow> {
        val rows = when (join) {
            is InnerJoin<E> if (correlatedJoinsEnabled && isCorrelatedJoin(join, leftSide)) -> {
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                leftSide.items.flatMap { row ->
                    val dataToJoin = resolve(join.data, row)
                    dataToJoin.items
                        .map { rowToJoin -> row + rowToJoin }
                        .filter { row -> joinCondition.invoke(row) ?: false }
                }
            }
            is InnerJoin<E> -> {
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                val dataToJoin = resolve(join.data)
                leftSide.items.flatMap { row ->
                    dataToJoin.items
                        .map { rowToJoin -> row + rowToJoin }
                        .filter { row -> joinCondition.invoke(row) ?: false }
                }
            }
            is LeftJoin<E> if (correlatedJoinsEnabled && isCorrelatedJoin(join, leftSide)) -> {
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                leftSide.items.flatMap { row ->
                    resolve(join.data, row).items
                        .map { rowToJoin -> row + rowToJoin }
                        .filter { row -> joinCondition.invoke(row) ?: false }
                        .ifEmpty { listOf(row + DataRow(join.data.columnNames.map { it to null })) }
                }
            }
            is LeftJoin<E> -> {
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                val dataToJoin = resolve(join.data)
                leftSide.items.flatMap { row ->
                    dataToJoin.items
                        .map { rowToJoin -> row + rowToJoin }
                        .filter { row -> joinCondition.invoke(row) ?: false }
                        .ifEmpty { listOf(row + DataRow(join.data.columnNames.map { it to null })) }
                }
            }
            is RightJoin<E> -> {
                val dataToJoin = resolve(join.data)
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                dataToJoin.items.flatMap { rowToJoin ->
                    leftSide.items
                        .map { row -> row + rowToJoin }
                        .filter { row -> joinCondition.invoke(row) ?: false }
                        .ifEmpty { listOf(DataRow(leftSide.columnNames.map { it to null }) + rowToJoin) }
                }
            }
            is CrossJoin<E> if (correlatedJoinsEnabled && isCorrelatedJoin(join, leftSide)) -> {
                leftSide.items.flatMap { row -> resolve(join.data, row).items.map { row + it } }
            }
            is CrossJoin<E> -> {
                val dataToJoin = resolve(join.data)
                leftSide.items.flatMap { row -> dataToJoin.items.map { row + it } }
            }
            else -> throw NotImplementedError("Simulation of ${join::class.qualifiedName} is not implemented.")
        }

        return if (rows.isNotEmpty()) ConstantData(orm, rows) else ConstantData.empty(orm, leftSide.columnNames + join.data.columnNames)
    }

    /**
     * Controls whether correlated [Join]s are supported. Defaults to `false`.
     *
     * Correlated joins are joins where the right side of the join depends on the current row of the left side.
     */
    var correlatedJoinsEnabled = false

    protected open fun isCorrelatedJoin(join: Join<E>, left: Data<E, *>) : Boolean = join.data.data.let { data ->
        when (data) {
            is Select<E, *> -> {
                (data.columns.orEmpty().map { it.value } + listOfNotNull(data.filter) + data.grouping)
                    .flatMap { subexpressionCollector.collectAllSubexpressions(it) }
                    .any { it is Column<E, *> && it.qualifiedName in left.columnNames }
            }
            is DataExpressionData<E, *> -> {
                subexpressionCollector
                    .collectAllSubexpressions(data.expression)
                    .any { it is Column<E, *> && it.qualifiedName in left.columnNames }
            }
            else -> false
        }
    }

    private fun Expression<E, *>.isAggregating(includeConstants: Boolean = false) : Boolean = when (this) {
        is Constant -> includeConstants
        is Aggregation -> true
        else -> subexpressionCollector.getSubexpressions(this).run { any { it.isAggregating(false) } && all { it.isAggregating(true) } }
    }

    val expressionEvaluatorForChecking = this.expressionEvaluator
}
