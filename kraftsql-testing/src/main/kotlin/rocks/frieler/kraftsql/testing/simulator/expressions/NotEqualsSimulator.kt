package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.NotEquals
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * Simulator for the [NotEquals] operator.
 *
 * @param E the [Engine] to simulate
 */
class NotEqualsSimulator<E : Engine<E>>(
    private val comparator: ConvertingComparator = ConvertingComparator(),
) : ExpressionSimulator<E, Boolean, NotEquals<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = NotEquals::class as KClass<NotEquals<E>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: NotEquals<E>) = { row: DataRow ->
        simulate(
            subexpressionCallbacks.simulateExpression(expression.left)(row),
            subexpressionCallbacks.simulateExpression(expression.right)(row))
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: NotEquals<E>) = { rows: List<DataRow> ->
        simulate(
            subexpressionCallbacks.simulateAggregation(expression.left)(rows),
            subexpressionCallbacks.simulateAggregation(expression.right)(rows))
    }

    private fun simulate(left: Any?, right: Any?) = comparator.compare(left, right).let { it != null && it != 0 }
}
