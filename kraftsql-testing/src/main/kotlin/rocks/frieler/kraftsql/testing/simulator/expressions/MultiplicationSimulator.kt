package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Multiplication
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * Simulator for the [Multiplication] operator.
 *
 * @param E the [Engine] to simulate
 */
class MultiplicationSimulator<E : Engine<E>> : ExpressionSimulator<E, Number?, Multiplication<E, Number>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Multiplication::class as KClass<out Multiplication<E, Number>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Multiplication<E, Number>): (DataRow) -> Number? {
        val left = subexpressionCallbacks.simulateExpression(expression.left)
        val right = subexpressionCallbacks.simulateExpression(expression.right)
        return { row -> simulate(left(row), right(row)) }
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Multiplication<E, Number>): (List<DataRow>) -> Number? {
        val left = subexpressionCallbacks.simulateAggregation(expression.left)
        val right = subexpressionCallbacks.simulateAggregation(expression.right)
        return { rows -> simulate(left(rows), right(rows)) }

    }

    private fun simulate(left: Number?, right: Number?): Number? = when {
        left == null || right == null -> null
        left is Int && right is Int -> Math.multiplyExact(left, right)
        left is Long && right is Long -> Math.multiplyExact(left, right)
        left is BigInteger && right is BigInteger -> left.multiply(right)
        left is Double && right is Double -> left * right
        left is BigDecimal && right is BigDecimal -> left.multiply(right)
        else -> throw NotImplementedError("Multiplying ${right::class.qualifiedName} with ${left::class.qualifiedName} is not implemented!")
    }
}
