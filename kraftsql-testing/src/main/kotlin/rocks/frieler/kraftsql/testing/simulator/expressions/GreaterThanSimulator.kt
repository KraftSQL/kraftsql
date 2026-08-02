package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.GreaterThan
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import kotlin.reflect.KClass

class GreaterThanSimulator<E : Engine<E>>(
    private val comparator: ConvertingComparator = ConvertingComparator(),
) : ExpressionSimulator<E, Boolean?, GreaterThan<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = GreaterThan::class as KClass<out GreaterThan<E>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: GreaterThan<E>): (DataRow) -> Boolean? {
        val leftExpression = subexpressionCallbacks.simulateExpression(expression.left)
        val rightExpression = subexpressionCallbacks.simulateExpression(expression.right)
        return { row -> simulate(leftExpression(row), rightExpression(row)) }
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: GreaterThan<E>): (List<DataRow>) -> Boolean? {
        val leftExpression = subexpressionCallbacks.simulateAggregation(expression.left)
        val rightExpression = subexpressionCallbacks.simulateAggregation(expression.right)
        return { rows -> simulate(leftExpression(rows), rightExpression(rows)) }
    }

    private fun simulate(left: Any?, right: Any?) = comparator.compare(left, right)?.let { it > 0 }
}
