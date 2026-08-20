package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Objects

/**
 * SQL '*' operator.
 *
 * Since the result type of multiplying two numbers depends on their common numeric type, this class has a protected
 * constructor and instead offers `invoke()`-factory methods and [times]-operators for each supported numeric type.
 *
 * @param E the [Engine] that implements this [Multiplication] and for which the SQL code is rendered
 * @param T the Kotlin type of the [Multiplication]'s result value
 * @param left the left-hand side of the '*'-expression
 * @param right the right-hand side of the '*'-expression
 */
open class Multiplication<E : Engine<E>, T : Number> protected constructor(
    val left: Expression<E, Number?>,
    val right: Expression<E, Number?>,
) : Expression<E, T?> {
    override fun sql() = "(${left.sql()})*(${right.sql()})"

    override fun equals(other: Any?) = other is Multiplication<E, T>
            && other.left == left
            && other.right == right

    override fun hashCode() = Objects.hash(left, right)

    companion object {
        /**
         * Creates a [Multiplication] of two [Int]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyIntAndInt")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Int?>, right: Expression<E, Int?>) = Multiplication<E, Int>(left, right)

        /**
         * Creates a [Multiplication] of two not-nullable [Int]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the not-nullable [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyIntAndIntNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Int>, right: Expression<E, Int>) = Multiplication<E, Int>(left, right).knownNotNull()

        /**
         * Creates a [Multiplication] of two [Long]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyLongAndLong")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Long?>, right: Expression<E, Long?>) = Multiplication<E, Long>(left, right)

        /**
         * Creates a [Multiplication] of two not-nullable [Long]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the not-nullable [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyLongAndLongNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Long>, right: Expression<E, Long>) = Multiplication<E, Long>(left, right).knownNotNull()

        /**
         * Creates a [Multiplication] of two [BigInteger]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyBigIntegerAndBigInteger")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigInteger?>, right: Expression<E, BigInteger?>) = Multiplication<E, BigInteger>(left, right)

        /**
         * Creates a [Multiplication] of two not-nullable [BigInteger]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the not-nullable [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyBigIntegerAndBigIntegerNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigInteger>, right: Expression<E, BigInteger>) = Multiplication<E, BigInteger>(left, right).knownNotNull()

        /**
         * Creates a [Multiplication] of two [Double]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyDoubleAndDouble")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Double?>, right: Expression<E, Double?>) = Multiplication<E, Double>(left, right)

        /**
         * Creates a [Multiplication] of two not-nullable [Double]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the not-nullable [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyDoubleAndDoubleNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Double>, right: Expression<E, Double>) = Multiplication<E, Double>(left, right).knownNotNull()

        /**
         * Creates a [Multiplication] of two [BigDecimal]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyBigDecimalAndBigDecimal")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigDecimal?>, right: Expression<E, BigDecimal?>) = Multiplication<E, BigDecimal>(left, right)

        /**
         * Creates a [Multiplication] of two not-nullable [BigDecimal]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '*'-expression
         * @param right the right-hand side of the '*'-expression
         * @return the not-nullable [Multiplication] of `left` and `right`
         */
        @JvmName("MultiplyBigDecimalAndBigDecimalNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigDecimal>, right: Expression<E, BigDecimal>) = Multiplication<E, BigDecimal>(left, right).knownNotNull()
    }
}

/**
 * Short operator syntax for [Multiplication] of two [Int]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Multiplication] of this and `another`
 */
@JvmName("TimesIntAndInt")
operator fun <E : Engine<E>> Expression<E, Int?>.times(another: Expression<E, Int?>) : Multiplication<E, Int> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two not-nullable [Int]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Multiplication] of this and `another`
 */
@JvmName("TimesIntAndIntNotNull")
operator fun <E : Engine<E>> Expression<E, Int>.times(another: Expression<E, Int>) : Expression<E, Int> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two [Long]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Multiplication] of this and `another`
 */
@JvmName("TimesLongAndLong")
operator fun <E : Engine<E>> Expression<E, Long?>.times(another: Expression<E, Long?>) : Multiplication<E, Long> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two not-nullable [Long]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Multiplication] of this and `another`
 */
@JvmName("TimesLongAndLongNotNull")
operator fun <E : Engine<E>> Expression<E, Long>.times(another: Expression<E, Long>) : Expression<E, Long> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two [BigInteger]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Multiplication] of this and `another`
 */
@JvmName("TimesBigIntegerAndBigInteger")
operator fun <E : Engine<E>> Expression<E, BigInteger?>.times(another: Expression<E, BigInteger?>) : Multiplication<E, BigInteger> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two not-nullable [BigInteger]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Multiplication] of this and `another`
 */
@JvmName("TimesBigIntegerAndBigIntegerNotNull")
operator fun <E : Engine<E>> Expression<E, BigInteger>.times(another: Expression<E, BigInteger>) : Expression<E, BigInteger> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two [Double]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Multiplication] of this and `another`
 */
@JvmName("TimesDoubleAndDouble")
operator fun <E : Engine<E>> Expression<E, Double?>.times(another: Expression<E, Double?>) : Multiplication<E, Double> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two not-nullable [Double]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Multiplication] of this and `another`
 */
@JvmName("TimesDoubleAndDoubleNotNull")
operator fun <E : Engine<E>> Expression<E, Double>.times(another: Expression<E, Double>) : Expression<E, Double> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two [BigDecimal]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Multiplication] of this and `another`
 */
@JvmName("TimesBigDecimalAndBigDecimal")
operator fun <E : Engine<E>> Expression<E, BigDecimal?>.times(another: Expression<E, BigDecimal?>) : Multiplication<E, BigDecimal> = Multiplication(this, another)

/**
 * Short operator syntax for [Multiplication] of two not-nullable [BigDecimal]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Multiplication] of this and `another`
 */
@JvmName("TimesBigDecimalAndBigDecimalNotNull")
operator fun <E : Engine<E>> Expression<E, BigDecimal>.times(another: Expression<E, BigDecimal>) : Expression<E, BigDecimal> = Multiplication(this, another)
