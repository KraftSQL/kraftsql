package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

class MaxSimulator<E : Engine<E>, T : Comparable<T>?> : AggregationSimulator<E, T?, Max<E, T>>("MAX") {
    @Suppress("UNCHECKED_CAST")
    override val expression = Max::class as KClass<out Max<E, T>>

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Max<E, T>): (List<DataRow>) -> T? {
        val subexpressionSimulation = subexpressionCallbacks.simulateExpression(expression.expression)
        return { rows -> rows.mapNotNull { subexpressionSimulation(it) }.maxOrNull() }
    }
}
