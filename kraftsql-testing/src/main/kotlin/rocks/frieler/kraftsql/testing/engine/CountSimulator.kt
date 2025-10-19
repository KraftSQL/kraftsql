package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.sql.SQLException
import kotlin.reflect.KClass

/**
 * Simulator for [Count] expressions.
 *
 * @param <E> the [Engine] to simulate
 */
class CountSimulator<E : Engine<E>> : ExpressionSimulator<E, Long, Count<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Count::class as KClass<out Count<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Count<E>): (DataRow) -> Long? {
        throw SQLException("COUNT must be used as an aggregating expression.")
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Count<E>): (List<DataRow>) -> Long? = { rows ->
        rows.size.toLong()
    }
}
