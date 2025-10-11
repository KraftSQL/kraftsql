package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Sum.Companion.SumAsBigDecimal
import rocks.frieler.kraftsql.expressions.Sum.Companion.SumAsDouble
import rocks.frieler.kraftsql.expressions.Sum.Companion.SumAsLong
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal
import java.sql.SQLException
import kotlin.reflect.KClass

/**
 * Simulator for [SumAsLong] expressions, the [rocks.frieler.kraftsql.expressions.Sum] that results in an integer-valued
 * result.
 *
 * @param <E> the [Engine] to simulate
 */
class SumAsLongSimulator<E : Engine<E>> : ExpressionSimulator<E, Long, SumAsLong<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = SumAsLong::class as KClass<out SumAsLong<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: SumAsLong<E>): (DataRow) -> Long? {
        throw SQLException("SUM must be used as an aggregating expression.")
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: SumAsLong<E>): (List<DataRow>) -> Long? = { rows ->
        rows.mapNotNull { row -> (subexpressionCallbacks.simulateExpression(expression.expression)(row) as Number?)?.toLong() }
            .reduceOrNull { a, b -> a + b }
    }
}

/**
 * Simulator for [SumAsDouble] expressions, the [rocks.frieler.kraftsql.expressions.Sum] that results in a double-valued
 * result.
 *
 * @param <E> the [Engine] to simulate
 */
class SumAsDoubleSimulator<E : Engine<E>> : ExpressionSimulator<E, Double, SumAsDouble<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = SumAsDouble::class as KClass<out SumAsDouble<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: SumAsDouble<E>): (DataRow) -> Double? {
        throw SQLException("SUM must be used as an aggregating expression.")
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: SumAsDouble<E>): (List<DataRow>) -> Double? = { rows ->
        rows.mapNotNull { row -> (subexpressionCallbacks.simulateExpression(expression.expression)(row) as Number?)?.toDouble() }
            .reduceOrNull { a, b -> a + b }
    }
}

/**
 * Simulator for [SumAsBigDecimal] expressions, the [rocks.frieler.kraftsql.expressions.Sum] that results in a
 * [BigDecimal]-valued result.
 *
 * @param <E> the [Engine] to simulate
 */
class SumAsBigDecimalSimulator<E : Engine<E>> : ExpressionSimulator<E, BigDecimal, SumAsBigDecimal<E>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = SumAsBigDecimal::class as KClass<out SumAsBigDecimal<E>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: SumAsBigDecimal<E>): (DataRow) -> BigDecimal? {
        throw SQLException("SUM must be used as an aggregating expression.")
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: SumAsBigDecimal<E>): (List<DataRow>) -> BigDecimal? = { rows ->
        rows.mapNotNull { row -> subexpressionCallbacks.simulateExpression(expression.expression)(row) as BigDecimal? }
            .reduceOrNull(BigDecimal::plus)
    }
}
