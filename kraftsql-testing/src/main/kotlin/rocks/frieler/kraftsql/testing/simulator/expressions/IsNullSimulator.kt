package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNull
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import kotlin.reflect.KClass

/**
 * Simulator for the [IsNull] operator.
 *
 * @param E the SQL [Engine]
 */
class IsNullSimulator<E : Engine<E>> : ExpressionSimulator<E, Boolean, IsNull<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = IsNull::class as KClass<IsNull<E>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: IsNull<E>): (DataRow) -> Boolean = { row ->
        simulate(subexpressionCallbacks.simulateExpression(expression.expression)(row))
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: IsNull<E>): (List<DataRow>) -> Boolean = { rows ->
        simulate(subexpressionCallbacks.simulateAggregation(expression.expression)(rows))
    }

    private fun simulate(value: Any?) = value == null
}
