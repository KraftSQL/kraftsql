package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Min
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for [Min] expressions.
 *
 * @param E the [Engine] to simulate
 * @param T the Kotlin type of the [Expression] to get the minimum of and thereby of its [Min]
 */
class MinSimulator<E : Engine<E>, T : Comparable<T>> : AggregationSimulator<E, T?, Min<E, T>>("MIN") {
    @Suppress("UNCHECKED_CAST")
    override val expression = Min::class as KClass<out Min<E, T>>

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Min<E, T>): (List<DataRow>) -> T? {
        val subexpressionSimulation = subexpressionCallbacks.simulateExpression(expression.expression)
        return { rows -> rows.mapNotNull { subexpressionSimulation(it) }.minOrNull() }
    }
}
