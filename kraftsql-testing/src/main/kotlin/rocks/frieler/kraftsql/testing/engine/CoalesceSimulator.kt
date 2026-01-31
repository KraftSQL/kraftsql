package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for [Coalesce] function.
 *
 * @param E the [Engine] to simulate
 * @param T the Kotlin result type of the [Coalesce] function and thereby its simulation
 */
class CoalesceSimulator<E : Engine<E>, T> : ExpressionSimulator<E, T, Coalesce<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Coalesce::class as KClass<out Coalesce<E, T>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Coalesce<E, T>): (DataRow) -> T {
        val subexpressionSimulations = expression.expressions.map { subexpressionCallbacks.simulateExpression(it) }
        return { row -> simulate(subexpressionSimulations.map { it(row) }) } }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Coalesce<E, T>): (List<DataRow>) -> T {
        val subexpressionSimulations = expression.expressions.map { subexpressionCallbacks.simulateAggregation(it) }
        return { rows -> simulate(subexpressionSimulations.map { it(rows) }) }
    }

    @Suppress("UNCHECKED_CAST") // T is only not nullable, when the last subexpression is not nullable, i.e. the last value is not null
    private fun simulate(values: List<T?>) : T = values.filterNotNull().firstOrNull() as T
}
