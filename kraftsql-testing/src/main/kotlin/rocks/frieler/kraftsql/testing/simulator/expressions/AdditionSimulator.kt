package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Addition
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * Simulator for the [Addition] operator.
 *
 * @param E the [Engine] to simulate
 */
class AdditionSimulator<E : Engine<E>> : ExpressionSimulator<E, Number?, Addition<E, Number>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Addition::class as KClass<out Addition<E, Number>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Addition<E, Number>): (DataRow) -> Number? {
        val left = subexpressionCallbacks.simulateExpression(expression.left)
        val right = subexpressionCallbacks.simulateExpression(expression.right)
        return { row -> simulate(left(row), right(row)) }
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Addition<E, Number>): (List<DataRow>) -> Number? {
        val left = subexpressionCallbacks.simulateAggregation(expression.left)
        val right = subexpressionCallbacks.simulateAggregation(expression.right)
        return { rows -> simulate(left(rows), right(rows)) }

    }

    private fun simulate(left: Number?, right: Number?): Number? = when {
        left == null || right == null -> null
        left is Int && right is Int -> Math.addExact(left, right)
        left is Long && right is Long -> Math.addExact(left, right)
        left is BigInteger && right is BigInteger -> left.add(right)
        left is Double && right is Double -> left + right
        left is BigDecimal && right is BigDecimal -> left.add(right)
        else -> throw NotImplementedError("Adding ${left::class.qualifiedName} and ${right::class.qualifiedName} is not implemented!")
    }
}
