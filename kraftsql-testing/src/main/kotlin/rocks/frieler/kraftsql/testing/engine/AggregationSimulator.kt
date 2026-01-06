package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Aggregation
import rocks.frieler.kraftsql.objects.DataRow
import java.sql.SQLException

abstract class AggregationSimulator<E : Engine<E>, out T, A : Aggregation<E, T>>(
    private val aggregationSQL: String,
) : ExpressionSimulator<E, T, A> {
    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: A): (DataRow) -> T {
        throw SQLException("$aggregationSQL must be used as an aggregating expression.")
    }
}
