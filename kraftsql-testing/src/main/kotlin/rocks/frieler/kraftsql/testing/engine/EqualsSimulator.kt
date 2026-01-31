package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.engine.ExpressionSimulator.SubexpressionCallbacks
import kotlin.reflect.KClass

/**
 * Simulator for the [Equals] operator.
 *
 * @param E the [Engine] to simulate
 */
class EqualsSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean, Equals<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Equals::class as KClass<Equals<E>>

    context(subexpressionCallbacks: SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Equals<E>) = { row: DataRow ->
        simulate(
            subexpressionCallbacks.simulateExpression(expression.left)(row),
            subexpressionCallbacks.simulateExpression(expression.right)(row))
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Equals<E>) = { rows: List<DataRow> ->
        simulate(
            subexpressionCallbacks.simulateAggregation(expression.left)(rows),
            subexpressionCallbacks.simulateAggregation(expression.right)(rows))
    }

    private fun simulate(left: Any?, right: Any?) = left != null && right != null && left == right
}
