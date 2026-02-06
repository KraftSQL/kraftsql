package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.And
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for the [And] operator.
 *
 * @param E the [Engine] to simulate
 */
class AndSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean, And<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = And::class as KClass<And<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: And<E>): (DataRow) -> Boolean {
        return { row -> simulate(
            subexpressionCallbacks.simulateExpression(expression.left)(row),
            subexpressionCallbacks.simulateExpression(expression.right)(row))
        }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: And<E>): (List<DataRow>) -> Boolean {
        return { rows -> simulate(
            subexpressionCallbacks.simulateAggregation(expression.left)(rows),
            subexpressionCallbacks.simulateAggregation(expression.right)(rows))
        }
    }

    private fun simulate(left: Boolean?, right: Boolean?) =
        left != null && right != null && left && right
}
