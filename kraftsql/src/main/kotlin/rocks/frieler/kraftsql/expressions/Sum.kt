package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.math.BigDecimal

abstract class Sum<E : Engine<E>, T : Number> protected constructor(
    val expression: Expression<E, *>,
) : Aggregation<E, T> {

    override fun sql() = "SUM(${expression.sql()})"

    override fun defaultColumnName() = "SUM(${expression.defaultColumnName()})"

    override fun equals(other: Any?) = other is Sum<E, T> && expression == other.expression

    override fun hashCode() = expression.hashCode()

    companion object {
        @JvmName("SumOfBytes")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, Byte>) = SumAsLong(expression)

        @JvmName("SumOfShorts")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, Short>) = SumAsLong(expression)

        @JvmName("SumOfInts")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, Int>) = SumAsLong(expression)

        @JvmName("SumOfLongs")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, Long>) = SumAsLong(expression)

        @JvmName("SumOfFloats")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, Float>) = SumAsDouble(expression)

        @JvmName("SumOfDoubles")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, Double>) = SumAsDouble(expression)

        @JvmName("SumOfBigDecimals")
        operator fun <E : Engine<E>> invoke(expression: Expression<E, BigDecimal>) = SumAsBigDecimal(expression)
    }
}

open class SumAsLong<E : Engine<E>>(expression: Expression<E, *>) : Sum<E, Long>(expression)

open class SumAsDouble<E : Engine<E>>(expression: Expression<E, *>) : Sum<E, Double>(expression)

open class SumAsBigDecimal<E : Engine<E>>(expression: Expression<E, *>) : Sum<E, BigDecimal>(expression)
