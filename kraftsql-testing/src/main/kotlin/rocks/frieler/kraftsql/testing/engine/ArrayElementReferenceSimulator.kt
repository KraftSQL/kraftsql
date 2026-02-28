package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.ArrayElementReference
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * [ExpressionSimulator] for [ArrayElementReference] expressions.
 *
 * @param E the [Engine] to simulate
 * @param T the type of the elements of the array
 */
open class ArrayElementReferenceSimulator<E : Engine<E>, T> : ExpressionSimulator<E, T?, ArrayElementReference<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = ArrayElementReference::class as KClass<out ArrayElementReference<E, T>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: ArrayElementReference<E, T>): (DataRow) -> T? {
        val arrayExpression = subexpressionCallbacks.simulateExpression(expression.array)
        val indexExpression = subexpressionCallbacks.simulateExpression(expression.index)
        return { row -> simulate(arrayExpression(row), indexExpression(row)) }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: ArrayElementReference<E, T>): (List<DataRow>) -> T? {
        val arrayExpression = subexpressionCallbacks.simulateAggregation(expression.array)
        val indexExpression = subexpressionCallbacks.simulateAggregation(expression.index)
        return { rows -> simulate(arrayExpression(rows), indexExpression(rows)) }
    }

    protected open fun simulate(array: Array<T>?, index: Int) : T? =
        if (array != null) {
            array[index - 1]
        } else {
            null
        }
}
