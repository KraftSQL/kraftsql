package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for [Count] expressions.
 *
 * @param <E> the [Engine] to simulate
 */
class CountSimulator<E : Engine<E>> : AggregationSimulator<E, Long, Count<E>>("COUNT") {
    @Suppress("UNCHECKED_CAST")
    override val expression = Count::class as KClass<out Count<E>>

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Count<E>): (List<DataRow>) -> Long? = { rows ->
        rows.size.toLong()
    }
}
