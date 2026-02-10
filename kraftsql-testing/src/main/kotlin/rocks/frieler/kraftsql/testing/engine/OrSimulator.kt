package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Or
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass


class OrSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean, Or<E>> {

    @Suppress("UNCHECKED_CAST")
    override val expression: KClass<out Or<E>> = Or::class as KClass<Or<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Or<E>): (DataRow) -> Boolean {
        return { rows ->
            simulate(
                subexpressionCallbacks.simulateExpression(expression.left)(rows),
                subexpressionCallbacks.simulateExpression(expression.right)(rows)
            )
        }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Or<E>): (List<DataRow>) -> Boolean {
        return { rows ->
            simulate(
                subexpressionCallbacks.simulateAggregation(expression.left)(rows),
                subexpressionCallbacks.simulateAggregation(expression.right)(rows)
            )
        }
    }

    private fun simulate(left: Boolean?, right: Boolean?) =
        left == null || right == null || left || right

}
