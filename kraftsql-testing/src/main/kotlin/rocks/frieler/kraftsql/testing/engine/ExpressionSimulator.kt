package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for a certain [Expression].
 *
 * @param <E> the [Engine] to simulate
 * @param <T> the Kotlin result type of the [Expression] and thereby its simulation
 * @param <X> the [Expression] type to simulate
 */
interface ExpressionSimulator<E : Engine<E>, T, X : Expression<E, T>> {
    /**
     * The [Expression] type to simulate.
     */
    val expression: KClass<out X>

    /**
     * Callbacks to simulate sub-expressions in the current evaluation context.
     *
     * @param <E> the [Engine] to simulate
     */
    interface SubexpressionCallbacks<E : Engine<E>> {
        fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T?

        context(groupExpressions: List<Expression<E, *>>)
        fun <T> simulateAggregation(expression: Expression<E, T>): (List<DataRow>) -> T?
    }

    /**
     * Provides a Kotlin simulation of the given [Expression] to be applied to a [DataRow].
     *
     * @param expression the [Expression] to simulate
     * @param subexpressionCallbacks [SubexpressionCallbacks] to simulate sub-expressions in the current evaluation context
     * @return a function that simulates the [Expression]
     */
    context(subexpressionCallbacks: SubexpressionCallbacks<E>)
    fun simulateExpression(expression: X): (DataRow) -> T?


    /**
     * Provides a Kotlin simulation of the given [Expression] as an aggregation to be applied to multiple [DataRow]s.
     *
     * For the [Expression] to aggregate multiple [DataRow]s, it must either be an
     * [rocks.frieler.kraftsql.expressions.Aggregation], one of the `groupExpressions` or the same must apply to all its
     * sub-expressions.
     *
     * @param expression the [Expression] to simulate
     * @param groupExpressions the [Expression]s that define the group of [DataRow]s to aggregate over
     * @param subexpressionCallbacks [SubexpressionCallbacks] to simulate sub-expressions in the current evaluation context
     * @return a function that simulates the [Expression]
     */
    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: SubexpressionCallbacks<E>)
    fun simulateAggregation(expression: X): (List<DataRow>) -> T?
}
