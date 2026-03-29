package rocks.frieler.kraftsql.testing.simulator.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Generic, configurable evaluator that simulates SQL [Expression]s using the registered [ExpressionSimulator]s.
 *
 * @param E the [Engine] to simulate
 */
open class GenericExpressionEvaluator<E : Engine<E>> {
    private val expressionSimulators: MutableMap<KClass<*>, ExpressionSimulator<E, *, *>> = mutableMapOf()

    /**
     * Registers an [ExpressionSimulator] for the given [Expression].
     *
     * @param expressionSimulator the additional [ExpressionSimulator]
     */
    fun <T, X: Expression<E, T>> registerExpressionSimulator(expressionSimulator: ExpressionSimulator<E, T, X>) {
        expressionSimulators[expressionSimulator.expression] = expressionSimulator
    }

    /**
     * Unregisters an [ExpressionSimulator] for the given [Expression].
     */
    fun unregisterExpressionSimulator(expression: KClass<out Expression<*, *>>) {
        expressionSimulators.remove(expression)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, X: Expression<E, T>> getExpressionSimulator(expression: X) =
        expressionSimulators.getOrElse(expression::class) {
            throw NotImplementedError("Simulation of a ${expression::class.qualifiedName} is not implemented.")
        } as ExpressionSimulator<E, T, X>

    init {
        registerExpressionSimulator(ConstantSimulator<E, Any?>())
        registerExpressionSimulator(ColumnSimulator<E, Any?>())
        registerExpressionSimulator(CastSimulator<E, Any?>())
        registerExpressionSimulator(IsNotNullSimulator())
        registerExpressionSimulator(EqualsSimulator())
        registerExpressionSimulator(LessOrEqualSimulator())
        registerExpressionSimulator(AndSimulator())
        registerExpressionSimulator(OrSimulator())
        registerExpressionSimulator(NotSimulator())
        registerExpressionSimulator(CoalesceSimulator<E, Any?>())
        registerExpressionSimulator(ArraySimulator<E, Any>())
        registerExpressionSimulator(ArrayElementReferenceSimulator<E, Any?>())
        registerExpressionSimulator(ArrayLengthSimulator())
        registerExpressionSimulator(RowSimulator())
        registerExpressionSimulator(CountSimulator())
        registerExpressionSimulator(MaxSimulator<E, Comparable<Comparable<*>>>())
        registerExpressionSimulator(SumAsLongSimulator())
        registerExpressionSimulator(SumAsDoubleSimulator())
        registerExpressionSimulator(SumAsBigDecimalSimulator())
    }

    /**
     * Builds a function that simulates the given [Expression] on a [DataRow].
     *
     * @param expression the [Expression] to simulate
     * @return a function that simulates the [Expression]
     */
    open fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T {
        context(
            object : ExpressionSimulator.SubexpressionCallbacks<E> {
                override fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T = { row ->
                    context(this) { getExpressionSimulator(expression).simulateExpression(expression)(row) }
                }

                context(groupExpressions: List<Expression<E, *>>)
                override fun <T> simulateAggregation(expression: Expression<E, T>) =
                    throw IllegalStateException("sub-expression cannot be an aggregation")
            }
        ) {
            return getExpressionSimulator(expression).simulateExpression(expression)
        }
    }

    /**
     * Builds a function that simulates the given [Expression] as an aggregation on a list of [DataRow]s.
     *
     * All leaves of the [Expression] tree must be [rocks.frieler.kraftsql.expressions.Constant]s,
     * [rocks.frieler.kraftsql.expressions.Aggregation]s, or one of the `groupExpressions`, by which the data is grouped
     * so they are constant for all [DataRow]s to aggregate over.
     *
     * @param expression the [Expression] to simulate
     * @param groupExpressions the [Expression]s which evaluate to the same value for all [DataRow]s to aggregate over
     * @return a function that simulates the [Expression] as part of an aggregation
     */
    open fun <T> simulateAggregation(expression: Expression<E, T>, groupExpressions: List<Expression<E, *>>): (List<DataRow>) -> T {
        context(
            groupExpressions,
            object : ExpressionSimulator.SubexpressionCallbacks<E> {
                override fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T = { row ->
                    context(this) { getExpressionSimulator(expression).simulateExpression(expression)(row) }
                }

                context(groupExpressions: List<Expression<E, *>>)
                override fun <T> simulateAggregation(expression: Expression<E, T>): (List<DataRow>) -> T = { rows ->
                    if (expression in groupExpressions) {
                        context(this) { getExpressionSimulator(expression).simulateExpression(expression)(rows.first()) }
                    } else {
                        context(groupExpressions, this) { getExpressionSimulator(expression).simulateAggregation(expression)(rows) }
                    }
                }
            }
        ) {
            return if (expression in groupExpressions) {
                { rows -> getExpressionSimulator(expression).simulateExpression(expression)(rows.first()) }
            } else {
                getExpressionSimulator(expression).simulateAggregation(expression)
            }
        }
    }
}
