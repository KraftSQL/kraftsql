package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.dql.CrossJoin
import rocks.frieler.kraftsql.dql.DataExpressionData
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Join
import rocks.frieler.kraftsql.dql.LeftJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.RightJoin
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.ifEmpty
import kotlin.collections.map
import kotlin.collections.orEmpty

open class GenericQueryEvaluator<E : Engine<E>>(
    protected val orm: SimulatorORMapping<E> = SimulatorORMapping(),
    protected val subexpressionCollector: SubexpressionCollector<E> = GenericSubexpressionCollector(),
    protected val expressionEvaluator: GenericExpressionEvaluator<E> = GenericExpressionEvaluator(),
) {
    context(activeState: EngineState<E>)
    fun selectRows(select: Select<E, *>, correlatedData: DataRow? = null) : List<DataRow> {
        var data = resolveQuerySource(select.source)

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
            val projections = select.columns!!
                .associate { (it.alias ?: it.value.defaultColumnName()) to expressionEvaluator.simulateExpression(it.value) }
            data.items.map { row ->
                DataRow(projections.map { (name, expression) -> name to expression.invoke(row) })
            }
        } else {
            data.items.toList()
        }

        return resultRows
    }

    context(activeState: EngineState<E>)
    protected open fun resolveQuerySource(source: QuerySource<E, *>, correlatedData: DataRow? = null) : ConstantData<E, DataRow> {
        val rows = fetchData(source.data, correlatedData)

        return if (rows.isEmpty()) {
            ConstantData.empty(orm, source.columnNames)
        } else if (source.alias == null) {
            ConstantData(orm, rows)
        } else {
            ConstantData(orm, rows.map { row ->
                DataRow(row.entries.map { (field, value) -> "${source.alias}${if (field.isNotEmpty()) ".$field" else ""}" to value })
            })
        }
    }

    context(activeState: EngineState<E>)
    protected open fun fetchData(data: Data<E, *>, correlatedData: DataRow?): List<DataRow> = when (data) {
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
            resolveQuerySource(QuerySource(expressionEvaluator.simulateExpression(data.expression).invoke(correlatedData ?: DataRow()))).items.toList()
        }
        else -> throw NotImplementedError("Fetching ${data::class.qualifiedName} is not implemented.")
    }

    context(activeState: EngineState<E>)
    private fun handleJoin(leftSide: ConstantData<E, DataRow>, join: Join<E>): ConstantData<E, DataRow> {
        val rows = when (join) {
            is InnerJoin<E> -> {
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                if (correlatedJoinsEnabled && isCorrelatedJoin(join, leftSide)) {
                    leftSide.items.flatMap { row ->
                        val dataToJoin = resolveQuerySource(join.data, row)
                        dataToJoin.items
                            .map { rowToJoin -> row + rowToJoin }
                            .filter { row -> joinCondition.invoke(row) ?: false }
                    }
                } else {
                    val dataToJoin = resolveQuerySource(join.data)
                    leftSide.items.flatMap { row ->
                        dataToJoin.items
                            .map { rowToJoin -> row + rowToJoin }
                            .filter { row -> joinCondition.invoke(row) ?: false }
                    }
                }
            }
            is LeftJoin<E> -> {
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                if (correlatedJoinsEnabled && isCorrelatedJoin(join, leftSide)) {
                    leftSide.items.flatMap { row ->
                        resolveQuerySource(join.data, row).items
                            .map { rowToJoin -> row + rowToJoin }
                            .filter { row -> joinCondition.invoke(row) ?: false }
                            .ifEmpty { listOf(row + DataRow(join.data.columnNames.map { it to null })) }
                    }
                } else {
                    val dataToJoin = resolveQuerySource(join.data)
                    leftSide.items.flatMap { row ->
                        dataToJoin.items
                            .map { rowToJoin -> row + rowToJoin }
                            .filter { row -> joinCondition.invoke(row) ?: false }
                            .ifEmpty { listOf(row + DataRow(join.data.columnNames.map { it to null })) }
                    }
                }
            }
            is RightJoin<E> -> {
                val dataToJoin = resolveQuerySource(join.data)
                val joinCondition = expressionEvaluator.simulateExpression(join.condition)
                dataToJoin.items.flatMap { rowToJoin ->
                    leftSide.items
                        .map { row -> row + rowToJoin }
                        .filter { row -> joinCondition.invoke(row) ?: false }
                        .ifEmpty { listOf(DataRow(leftSide.columnNames.map { it to null }) + rowToJoin) }
                }
            }
            is CrossJoin<E> -> {
                if (correlatedJoinsEnabled && isCorrelatedJoin(join, leftSide)) {
                    leftSide.items.flatMap { row -> resolveQuerySource(join.data, row).items.map { row + it } }
                } else {
                    val dataToJoin = resolveQuerySource(join.data)
                    leftSide.items.flatMap { row -> dataToJoin.items.map { row + it } }
                }
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

    internal val expressionEvaluatorForChecking = this.expressionEvaluator
}
