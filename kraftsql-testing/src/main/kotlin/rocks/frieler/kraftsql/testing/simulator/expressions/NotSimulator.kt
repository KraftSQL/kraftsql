package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Not
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for the [Not] operator.
 *
 * @param E the [Engine] to simulate
 */
open class NotSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean?, Not<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Not::class as KClass<Not<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Not<E>): (DataRow) -> Boolean? {
        return { row -> simulate(subexpressionCallbacks.simulateExpression(expression.expression)(row)) }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Not<E>): (List<DataRow>) -> Boolean? {
        return { rows -> simulate(subexpressionCallbacks.simulateAggregation(expression.expression)(rows)) }
    }

    protected open fun simulate(value: Boolean?) : Boolean? =
        value?.not() ?: false
}
