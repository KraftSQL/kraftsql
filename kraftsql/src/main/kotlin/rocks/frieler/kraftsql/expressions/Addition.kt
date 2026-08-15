package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Objects

/**
 * SQL '+' operator.
 *
 * Since the result type of adding two numbers depends on their common numeric type, this class has a protected
 * constructor and instead offers `invoke()`-factory methods and [plus]-operators for each supported numeric type.
 *
 * @param E the [Engine] that implements this [Addition] and for which the SQL code is rendered
 * @param T the Kotlin type of the [Addition]'s result value
 * @param left the left-hand side of the '+'-expression
 * @param right the right-hand side of the '+'-expression
 */
open class Addition<E : Engine<E>, T : Number> protected constructor(
    val left: Expression<E, Number?>,
    val right: Expression<E, Number?>,
) : Expression<E, T?> {
    override fun sql() = "(${left.sql()})+(${right.sql()})"

    override fun equals(other: Any?) = other is Addition<E, T>
            && other.left == left
            && other.right == right

    override fun hashCode() = Objects.hash(left, right)

    companion object {
        /**
         * Creates an [Addition] of two [Int]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the [Addition] of `left` and `right`
         */
        @JvmName("AddIntAndInt")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Int?>, right: Expression<E, Int?>) = Addition<E, Int>(left, right)

        /**
         * Creates an [Addition] of two not-nullable [Int]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the not-nullable [Addition] of `left` and `right`
         */
        @JvmName("AddIntAndIntNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Int>, right: Expression<E, Int>) = Addition<E, Int>(left, right).knownNotNull()

        /**
         * Creates an [Addition] of two [Long]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the [Addition] of `left` and `right`
         */
        @JvmName("AddLongAndLong")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Long?>, right: Expression<E, Long?>) = Addition<E, Long>(left, right)

        /**
         * Creates an [Addition] of two not-nullable [Long]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the not-nullable [Addition] of `left` and `right`
         */
        @JvmName("AddLongAndLongNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Long>, right: Expression<E, Long>) = Addition<E, Long>(left, right).knownNotNull()

        /**
         * Creates an [Addition] of two [BigInteger]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the [Addition] of `left` and `right`
         */
        @JvmName("AddBigIntegerAndBigInteger")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigInteger?>, right: Expression<E, BigInteger?>) = Addition<E, BigInteger>(left, right)

        /**
         * Creates an [Addition] of two not-nullable [BigInteger]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the not-nullable [Addition] of `left` and `right`
         */
        @JvmName("AddBigIntegerAndBigIntegerNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigInteger>, right: Expression<E, BigInteger>) = Addition<E, BigInteger>(left, right).knownNotNull()

        /**
         * Creates an [Addition] of two [Double]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the [Addition] of `left` and `right`
         */
        @JvmName("AddDoubleAndDouble")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Double?>, right: Expression<E, Double?>) = Addition<E, Double>(left, right)

        /**
         * Creates an [Addition] of two not-nullable [Double]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the not-nullable [Addition] of `left` and `right`
         */
        @JvmName("AddDoubleAndDoubleNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, Double>, right: Expression<E, Double>) = Addition<E, Double>(left, right).knownNotNull()

        /**
         * Creates an [Addition] of two [BigDecimal]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the [Addition] of `left` and `right`
         */
        @JvmName("AddBigDecimalAndBigDecimal")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigDecimal?>, right: Expression<E, BigDecimal?>) = Addition<E, BigDecimal>(left, right)

        /**
         * Creates an [Addition] of two not-nullable [BigDecimal]-valued [Expression]s.
         *
         * @param E the [Engine] targeted
         * @param left the left-hand side of the '+'-expression
         * @param right the right-hand side of the '+'-expression
         * @return the not-nullable [Addition] of `left` and `right`
         */
        @JvmName("AddBigDecimalAndBigDecimalNotNull")
        operator fun <E : Engine<E>> invoke(left: Expression<E, BigDecimal>, right: Expression<E, BigDecimal>) = Addition<E, BigDecimal>(left, right).knownNotNull()
    }
}

/**
 * Short operator syntax for [Addition] of two [Int]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Addition] of this and `another`
 */
@JvmName("PlusIntAndInt")
operator fun <E : Engine<E>> Expression<E, Int?>.plus(another: Expression<E, Int?>) : Addition<E, Int> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two not-nullable [Int]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Addition] of this and `another`
 */
@JvmName("PlusIntAndIntNotNull")
operator fun <E : Engine<E>> Expression<E, Int>.plus(another: Expression<E, Int>) : Expression<E, Int> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two [Long]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Addition] of this and `another`
 */
@JvmName("PlusLongAndLong")
operator fun <E : Engine<E>> Expression<E, Long?>.plus(another: Expression<E, Long?>) : Addition<E, Long> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two not-nullable [Long]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Addition] of this and `another`
 */
@JvmName("PlusLongAndLongNotNull")
operator fun <E : Engine<E>> Expression<E, Long>.plus(another: Expression<E, Long>) : Expression<E, Long> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two [BigInteger]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Addition] of this and `another`
 */
@JvmName("PlusBigIntegerAndBigInteger")
operator fun <E : Engine<E>> Expression<E, BigInteger?>.plus(another: Expression<E, BigInteger?>) : Addition<E, BigInteger> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two not-nullable [BigInteger]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Addition] of this and `another`
 */
@JvmName("PlusBigIntegerAndBigIntegerNotNull")
operator fun <E : Engine<E>> Expression<E, BigInteger>.plus(another: Expression<E, BigInteger>) : Expression<E, BigInteger> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two [Double]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Addition] of this and `another`
 */
@JvmName("PlusDoubleAndDouble")
operator fun <E : Engine<E>> Expression<E, Double?>.plus(another: Expression<E, Double?>) : Addition<E, Double> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two not-nullable [Double]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Addition] of this and `another`
 */
@JvmName("PlusDoubleAndDoubleNotNull")
operator fun <E : Engine<E>> Expression<E, Double>.plus(another: Expression<E, Double>) : Expression<E, Double> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two [BigDecimal]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the [Addition] of this and `another`
 */
@JvmName("PlusBigDecimalAndBigDecimal")
operator fun <E : Engine<E>> Expression<E, BigDecimal?>.plus(another: Expression<E, BigDecimal?>) : Addition<E, BigDecimal> = Addition(this, another)

/**
 * Short operator syntax for [Addition] of two not-nullable [BigDecimal]-valued [Expression]s.
 *
 * @param E the [Engine] targeted
 * @return the not-nullable [Addition] of this and `another`
 */
@JvmName("PlusBigDecimalAndBigDecimalNotNull")
operator fun <E : Engine<E>> Expression<E, BigDecimal>.plus(another: Expression<E, BigDecimal>) : Expression<E, BigDecimal> = Addition(this, another)
