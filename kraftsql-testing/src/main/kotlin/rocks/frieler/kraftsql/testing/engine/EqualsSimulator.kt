package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.engine.ExpressionSimulator.SubexpressionCallbacks
import java.math.BigDecimal
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

    private fun simulate(left: Any?, right: Any?): Boolean =
        if (left == null || right == null) {
            false
        } else if (left is Number && right is Number) {
            compareNumbers(left, right)
        } else {
            left == right
        }

    @Suppress("ComplexMethod")
    private fun compareNumbers(left: Number, right: Number): Boolean =
        when (left) {
            is Byte -> when (right) {
                is Byte -> left == right
                is Short -> left.toShort() == right
                is Int -> left.toInt() == right
                is Long -> left.toLong() == right
                is BigDecimal -> BigDecimal.valueOf(left.toLong()) == right
                else -> false
            }
            is Short -> when (right) {
                is Byte -> left == right.toShort()
                is Short -> left == right
                is Int -> left.toInt() == right
                is Long -> left.toLong() == right
                is BigDecimal -> BigDecimal.valueOf(left.toLong()) == right
                else -> false
            }
            is Int -> when (right) {
                is Byte -> left == right.toInt()
                is Short -> left == right.toInt()
                is Int -> left == right
                is Long -> left.toLong() == right
                is BigDecimal -> BigDecimal.valueOf(left.toLong()) == right
                else -> false
            }
            is Long -> when (right) {
                is Byte -> left == right.toLong()
                is Short -> left == right.toLong()
                is Int -> left == right.toLong()
                is Long -> left == right
                is BigDecimal -> BigDecimal.valueOf(left) == right
                else -> false
            }
            is Float -> when (right) {
                is Float -> left == right
                is Double -> left.toDouble() == right
                is BigDecimal -> BigDecimal.valueOf(left.toDouble()) == right
                else -> false
            }
            is Double -> when (right) {
                is Float -> left == right.toDouble()
                is Double -> left == right
                is BigDecimal -> BigDecimal.valueOf(left) == right
                else -> false
            }
            is BigDecimal -> when (right) {
                is Byte -> left == BigDecimal.valueOf(right.toLong())
                is Short -> left == BigDecimal.valueOf(right.toLong())
                is Int -> left == BigDecimal.valueOf(right.toLong())
                is Long -> left == BigDecimal.valueOf(right)
                is Float -> left == BigDecimal.valueOf(right.toDouble())
                is Double -> left == BigDecimal.valueOf(right)
                is BigDecimal -> left == right
                else -> false
            }
            else -> false
        }
}
