package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Or
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for the [Or] operator.
 *
 * @param E the [Engine] to simulate
 */
open class OrSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean?, Or<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Or::class as KClass<Or<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Or<E>): (DataRow) -> Boolean? {
        return { row -> simulate(
            subexpressionCallbacks.simulateExpression(expression.left)(row),
            subexpressionCallbacks.simulateExpression(expression.right)(row))
        }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Or<E>): (List<DataRow>) -> Boolean? {
        return { rows -> simulate(
            subexpressionCallbacks.simulateAggregation(expression.left)(rows),
            subexpressionCallbacks.simulateAggregation(expression.right)(rows))
        }
    }

    protected open fun simulate(left: Boolean?, right: Boolean?) : Boolean? =
        left ?: false || right ?: false
}
