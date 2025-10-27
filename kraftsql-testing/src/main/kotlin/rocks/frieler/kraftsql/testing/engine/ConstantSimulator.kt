package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for [Constant] expressions.
 *
 * @param <E> the [Engine] to simulate
 * @param <T> the Kotlin type of the [Constant]
 */
open class ConstantSimulator<E : Engine<E>, T> : ExpressionSimulator<E, T, Constant<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Constant::class as KClass<out Constant<E, T>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Constant<E, T>): (DataRow) -> T = { _ -> simulate(expression) }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Constant<E, T>): (List<DataRow>) -> T = { _ -> simulate(expression) }

    private fun simulate(expression: Constant<E, T>): T = expression.value
}
