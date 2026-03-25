package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.LessOrEqual
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

class LessOrEqualSimulator<E : Engine<E>>(
    private val comparator: ConvertingComparator = ConvertingComparator(),
) : ExpressionSimulator<E, Boolean?, LessOrEqual<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = LessOrEqual::class as KClass<out LessOrEqual<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: LessOrEqual<E>): (DataRow) -> Boolean? {
        val leftExpression = subexpressionCallbacks.simulateExpression(expression.left)
        val rightExpression = subexpressionCallbacks.simulateExpression(expression.right)
        return { row -> simulate(leftExpression(row), rightExpression(row)) }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: LessOrEqual<E>): (List<DataRow>) -> Boolean? {
        val leftExpression = subexpressionCallbacks.simulateAggregation(expression.left)
        val rightExpression = subexpressionCallbacks.simulateAggregation(expression.right)
        return { rows -> simulate(leftExpression(rows), rightExpression(rows)) }
    }

    private fun simulate(left: Any?, right: Any?) = comparator.compare(left, right)?.let { it <= 0 }
}
