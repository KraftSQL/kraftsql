package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

open class GenericExpressionEvaluator<E : Engine<E>> {
    private val expressionSimulators: MutableMap<KClass<*>, ExpressionSimulator<E, *, *>> = mutableMapOf()

    fun <T, X: Expression<E, T>> registerExpressionSimulator(expressionSimulator: ExpressionSimulator<E, T, X>) {
        expressionSimulators[expressionSimulator.expression] = expressionSimulator
    }

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

    open fun <T> simulateAggregation(expression: Expression<E, T>, groupExpressions: List<Expression<E, *>>): (List<DataRow>) -> T {
        context(
            groupExpressions,
            object : ExpressionSimulator.SubexpressionCallbacks<E> {
                override fun <T> simulateExpression(expression: Expression<E, T>): (DataRow) -> T = { row ->
                    context(this) { getExpressionSimulator(expression).simulateExpression(expression)(row) }
                }

                context(groupExpressions: List<Expression<E, *>>)
                override fun <T> simulateAggregation(expression: Expression<E, T>): (List<DataRow>) -> T = { rows ->
                    context(groupExpressions, this) {
                        if (expression in groupExpressions) {
                            getExpressionSimulator(expression).simulateExpression(expression)(rows.first())
                        } else {
                            getExpressionSimulator(expression).simulateAggregation(expression)(rows)
                        }
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
