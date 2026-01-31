package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for a [Row] expression.
 *
 * In a simulation, the [Row] expression always returns a [DataRow]. The mapping to another Kotlin class is done by
 * the [SimulatorORMapping].
 *
 * @param E the [Engine] to simulate
 */
open class RowSimulator<E : Engine<E>> : ExpressionSimulator<E, DataRow?, Row<E, DataRow?>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Row::class as KClass<out Row<E, DataRow?>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Row<E, DataRow?>): (DataRow) -> DataRow? = { row ->
        simulate(expression.values?.map { (name, value) -> name to subexpressionCallbacks.simulateExpression(value)(row) })
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Row<E, DataRow?>): (List<DataRow>) -> DataRow? = { rows ->
        simulate(expression.values?.map { (name, value) -> name to subexpressionCallbacks.simulateAggregation(value)(rows) })
    }

    private fun simulate(values: Iterable<Pair<String, *>>?): DataRow? =
        if (values == null) null else DataRow(values)
}
