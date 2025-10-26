package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.math.BigDecimal

/**
 * The `SUM()` [Aggregation] that sums the values of an [Expression].
 *
 * The SQL `SUM()` expression magically converts and sums any numbers, and the returned sum's type depends on the actual
 * values. Since this is not directly representable in Kotlin, this is an abstract class with factory methods for
 * different implementations for each numeric type.
 *
 * @param <E> the [Engine] that implements [Sum] and for which the SQL code is rendered
 * @param <T> the Kotlin type of the [Sum]'s result value
 * @param expression the [Expression] to sum the values of
 */
abstract class Sum<E : Engine<E>, T : Number> protected constructor(
    val expression: Expression<E, *>,
) : Aggregation<E, T?> {

    override fun sql() = "SUM(${expression.sql()})"

    override fun defaultColumnName() = "SUM(${expression.defaultColumnName()})"

    override fun equals(other: Any?) = other is Sum<E, T> && expression == other.expression

    override fun hashCode() = expression.hashCode()

    companion object {
        open class SumAsLong<E : Engine<E>>(expression: Expression<E, *>) : Sum<E, Long>(expression)

        /**
         * Sums the values of a byte-valued [Expression] as a [Long].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfBytes")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out Byte?>) = SumAsLong(expression)

        /**
         * Sums the values of a short-valued [Expression] as a [Long].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfShorts")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out Short?>) = SumAsLong(expression)

        /**
         * Sums the values of an int-valued [Expression] as a [Long].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfInts")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out Int?>) = SumAsLong(expression)

        /**
         * Sums the values of a long-valued [Expression] as a [Long].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfLongs")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out Long?>) = SumAsLong(expression)

        open class SumAsDouble<E : Engine<E>>(expression: Expression<E, *>) : Sum<E, Double>(expression)

        /**
         * Sums the values of a float-valued [Expression] as a [Double].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfFloats")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out Float?>) = SumAsDouble(expression)

        /**
         * Sums the values of a double-valued [Expression] as a [Double].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfDoubles")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out Double?>) = SumAsDouble(expression)

        open class SumAsBigDecimal<E : Engine<E>>(expression: Expression<E, *>) : Sum<E, BigDecimal>(expression)

        /**
         * Sums the values of a [BigDecimal]-valued [Expression] as a [BigDecimal].
         *
         * @param expression the [Expression] to sum the values of
         */
        @JvmName("SumOfBigDecimals")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, out BigDecimal?>) = SumAsBigDecimal(expression)
    }
}
