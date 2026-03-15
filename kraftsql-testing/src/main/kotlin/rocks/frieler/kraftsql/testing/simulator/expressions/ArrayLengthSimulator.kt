package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * [ExpressionSimulator] for [ArrayLength] expressions.
 *
 * @param E the [Engine] to simulate
 */
class ArrayLengthSimulator<E : Engine<E>> : ExpressionSimulator<E, Int?, ArrayLength<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = ArrayLength::class as KClass<out ArrayLength<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: ArrayLength<E>): (DataRow) -> Int? {
        val arrayExpression = subexpressionCallbacks.simulateExpression(expression.array)
        return { row -> simulate(arrayExpression(row)) }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: ArrayLength<E>): (List<DataRow>) -> Int? {
        val arrayExpression = subexpressionCallbacks.simulateAggregation(expression.array)
        return { rows -> simulate(arrayExpression(rows)) }
    }

    private fun simulate(array : Array<*>?) : Int? = array?.size
}
