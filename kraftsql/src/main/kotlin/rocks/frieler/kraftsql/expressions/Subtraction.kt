package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Objects

/**
 * SQL '-' operator.
 *
 * Since the result type of subtracting two numbers depends on their common numeric type, this class has a protected
 * constructor and instead offers `invoke()`-factory methods and [minus]-operators for each supported numeric type.
 *
 * @param E the [Engine] that implements this [Subtraction] and for which the SQL code is rendered
 * @param T the Kotlin type of the [Subtraction]'s result value
 * @param left the left-hand side of the '-'-expression
 * @param right the right-hand side of the '-'-expression
 */
open class Subtraction<E : Engine<E>, T : Number> protected constructor(
    val left: Expression<E, Number?>,
    val right: Expression<E, Number?>,
) : Expression<E, T?> {
    override fun sql() = "(${left.sql()})-(${right.sql()})"

    override fun equals(other: Any?) = other is Subtraction<E, T>
            && other.left == left
            && other.right == right

    override fun hashCode() = Objects.hash(left, right)

    companion object {
        /**
         * Creates a [Subtraction] of two [Int]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractIntAndInt")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Int?>, right: Expression<E, Int?>) = Subtraction<E, Int>(left, right)

        /**
         * Creates a [Subtraction] of two not-nullable [Int]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the not-nullable [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractIntAndIntNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Int>, right: Expression<E, Int>) = Subtraction<E, Int>(left, right).knownNotNull()

        /**
         * Creates a [Subtraction] of two [Long]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractLongAndLong")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Long?>, right: Expression<E, Long?>) = Subtraction<E, Long>(left, right)

        /**
         * Creates a [Subtraction] of two not-nullable [Long]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the not-nullable [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractLongAndLongNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Long>, right: Expression<E, Long>) = Subtraction<E, Long>(left, right).knownNotNull()

        /**
         * Creates a [Subtraction] of two [BigInteger]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractBigIntegerAndBigInteger")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigInteger?>, right: Expression<E, BigInteger?>) = Subtraction<E, BigInteger>(left, right)

        /**
         * Creates a [Subtraction] of two not-nullable [BigInteger]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the not-nullable [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractBigIntegerAndBigIntegerNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigInteger>, right: Expression<E, BigInteger>) = Subtraction<E, BigInteger>(left, right).knownNotNull()

        /**
         * Creates a [Subtraction] of two [Double]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractDoubleAndDouble")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Double?>, right: Expression<E, Double?>) = Subtraction<E, Double>(left, right)

        /**
         * Creates a [Subtraction] of two not-nullable [Double]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the not-nullable [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractDoubleAndDoubleNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Double>, right: Expression<E, Double>) = Subtraction<E, Double>(left, right).knownNotNull()

        /**
         * Creates a [Subtraction] of two [BigDecimal]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractBigDecimalAndBigDecimal")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigDecimal?>, right: Expression<E, BigDecimal?>) = Subtraction<E, BigDecimal>(left, right)

        /**
         * Creates a [Subtraction] of two not-nullable [BigDecimal]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '-'-expression
         * @param right the right-hand side of the '-'-expression
         * @return the not-nullable [Subtraction] of `left` and `right`
         */
        @JvmName("SubtractBigDecimalAndBigDecimalNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigDecimal>, right: Expression<E, BigDecimal>) = Subtraction<E, BigDecimal>(left, right).knownNotNull()
    }
}

/**
 * Short operator syntax for [Subtraction] of two [Int]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Subtraction] of this and `another`
 */
@JvmName("MinusIntAndInt")
operator fun <E : Engine<E>> Expression<E, Int?>.minus(another: Expression<E, Int?>) : Subtraction<E, Int> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two not-nullable [Int]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Subtraction] of this and `another`
 */
@JvmName("MinusIntAndIntNotNull")
operator fun <E : Engine<E>> Expression<E, Int>.minus(another: Expression<E, Int>) : Expression<E, Int> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two [Long]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Subtraction] of this and `another`
 */
@JvmName("MinusLongAndLong")
operator fun <E : Engine<E>> Expression<E, Long?>.minus(another: Expression<E, Long?>) : Subtraction<E, Long> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two not-nullable [Long]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Subtraction] of this and `another`
 */
@JvmName("MinusLongAndLongNotNull")
operator fun <E : Engine<E>> Expression<E, Long>.minus(another: Expression<E, Long>) : Expression<E, Long> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two [BigInteger]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Subtraction] of this and `another`
 */
@JvmName("MinusBigIntegerAndBigInteger")
operator fun <E : Engine<E>> Expression<E, BigInteger?>.minus(another: Expression<E, BigInteger?>) : Subtraction<E, BigInteger> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two not-nullable [BigInteger]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Subtraction] of this and `another`
 */
@JvmName("MinusBigIntegerAndBigIntegerNotNull")
operator fun <E : Engine<E>> Expression<E, BigInteger>.minus(another: Expression<E, BigInteger>) : Expression<E, BigInteger> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two [Double]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Subtraction] of this and `another`
 */
@JvmName("MinusDoubleAndDouble")
operator fun <E : Engine<E>> Expression<E, Double?>.minus(another: Expression<E, Double?>) : Subtraction<E, Double> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two not-nullable [Double]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Subtraction] of this and `another`
 */
@JvmName("MinusDoubleAndDoubleNotNull")
operator fun <E : Engine<E>> Expression<E, Double>.minus(another: Expression<E, Double>) : Expression<E, Double> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two [BigDecimal]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Subtraction] of this and `another`
 */
@JvmName("MinusBigDecimalAndBigDecimal")
operator fun <E : Engine<E>> Expression<E, BigDecimal?>.minus(another: Expression<E, BigDecimal?>) : Subtraction<E, BigDecimal> = Subtraction(this, another)

/**
 * Short operator syntax for [Subtraction] of two not-nullable [BigDecimal]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Subtraction] of this and `another`
 */
@JvmName("MinusBigDecimalAndBigDecimalNotNull")
operator fun <E : Engine<E>> Expression<E, BigDecimal>.minus(another: Expression<E, BigDecimal>) : Expression<E, BigDecimal> = Subtraction(this, another)
