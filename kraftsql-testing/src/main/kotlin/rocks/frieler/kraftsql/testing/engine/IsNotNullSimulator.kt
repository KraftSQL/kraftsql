package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for the [IsNotNull] operator.
 *
 * @param E the SQL [Engine]
 */
class IsNotNullSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean, IsNotNull<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = IsNotNull::class as KClass<IsNotNull<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: IsNotNull<E>): (DataRow) -> Boolean = { row ->
        simulate(subexpressionCallbacks.simulateExpression(expression.expression)(row))
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: IsNotNull<E>): (List<DataRow>) -> Boolean = { rows ->
        simulate(subexpressionCallbacks.simulateAggregation(expression.expression)(rows))
    }

    private fun simulate(value: Any?) = value != null
}
