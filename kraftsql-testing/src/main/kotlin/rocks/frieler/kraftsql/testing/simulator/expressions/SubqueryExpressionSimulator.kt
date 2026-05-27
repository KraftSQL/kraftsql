package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.SubqueryExpression
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import rocks.frieler.kraftsql.testing.simulator.engine.GenericQueryEvaluator
import java.sql.SQLException
import kotlin.reflect.KClass

/**
 * Simulator for [SubqueryExpression]s.
 *
 * Noteworthy limitation: Subqueries are not correlated, i.e. their evaluation has no access to the surrounding query.
 *
 * @param E the [Engine] to simulate
 * @param T the Kotlin type of the [SubqueryExpression]'s result value and thereby of its simulation
 * @param queryEvaluator the [GenericQueryEvaluator] to evaluate the wrapped subquery
 */
class SubqueryExpressionSimulator<E : Engine<E>, T>(
    private val queryEvaluator: GenericQueryEvaluator<E>,
) : ExpressionSimulator<E, T?, SubqueryExpression<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = SubqueryExpression::class as KClass<SubqueryExpression<E, T>>

    context(state: EngineState<E>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: SubqueryExpression<E, T>): (DataRow) -> T? {
        return { simulate(expression) }
    }

    context(state: EngineState<E>, groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: SubqueryExpression<E, T>): (List<DataRow>) -> T? {
        return { simulate(expression) }
    }

    context(activeState: EngineState<E>)
    private fun simulate(expression: SubqueryExpression<E, T>): T? {
        val singleRow = queryEvaluator.selectRows(expression.subquery)
            .also { if (it.isEmpty()) return null }
            .also { if (it.size > 1) throw SQLException("Subquery used as an expression returned more than one row (${it.size}).") }
            .single()
        val singleEntry = singleRow.entries.toList()
            .also { if (it.size != 1) throw SQLException("Subquery used as an expression returned a row with not exactly one value (${it.size}).") }
            .single()
        @Suppress("UNCHECKED_CAST")
        return singleEntry.second as T?
    }
}
