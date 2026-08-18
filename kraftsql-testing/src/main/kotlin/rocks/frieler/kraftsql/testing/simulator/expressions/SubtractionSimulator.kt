package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Subtraction
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * Simulator for the [Subtraction] operator.
 *
 * @param E the [Engine] to simulate
 */
class SubtractionSimulator<E : Engine<E>> : ExpressionSimulator<E, Number?, Subtraction<E, Number>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Subtraction::class as KClass<out Subtraction<E, Number>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Subtraction<E, Number>): (DataRow) -> Number? {
        val left = subexpressionCallbacks.simulateExpression(expression.left)
        val right = subexpressionCallbacks.simulateExpression(expression.right)
        return { row -> simulate(left(row), right(row)) }
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Subtraction<E, Number>): (List<DataRow>) -> Number? {
        val left = subexpressionCallbacks.simulateAggregation(expression.left)
        val right = subexpressionCallbacks.simulateAggregation(expression.right)
        return { rows -> simulate(left(rows), right(rows)) }

    }

    private fun simulate(left: Number?, right: Number?): Number? = when {
        left == null || right == null -> null
        left is Int && right is Int -> Math.subtractExact(left, right)
        left is Long && right is Long -> Math.subtractExact(left, right)
        left is BigInteger && right is BigInteger -> left.subtract(right)
        left is Double && right is Double -> left - right
        left is BigDecimal && right is BigDecimal -> left.subtract(right)
        else -> throw NotImplementedError("Subtracting ${right::class.qualifiedName} from ${left::class.qualifiedName} is not implemented!")
    }
}
