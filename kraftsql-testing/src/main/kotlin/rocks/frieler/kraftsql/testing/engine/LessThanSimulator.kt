package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.LessThan
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

class LessThanSimulator<E : Engine<E>>(
    private val comparator: ConvertingComparator = ConvertingComparator(),
) : ExpressionSimulator<E, Boolean?, LessThan<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = LessThan::class as KClass<out LessThan<E>>


    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: LessThan<E>): (DataRow) -> Boolean? {
        val leftExpression = subexpressionCallbacks.simulateExpression(expression.left)
        val rightExpression = subexpressionCallbacks.simulateExpression(expression.right)
        return { row -> simulate(leftExpression(row), rightExpression(row)) }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: LessThan<E>): (List<DataRow>) -> Boolean? {
        val leftExpression = subexpressionCallbacks.simulateAggregation(expression.left)
        val rightExpression = subexpressionCallbacks.simulateAggregation(expression.right)
        return { rows -> simulate(leftExpression(rows), rightExpression(rows)) }
    }

    private fun simulate(left: Any?, right: Any?) = comparator.compare(left, right)?.let { it < 0 }
}
