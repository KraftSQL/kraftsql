package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * SQL 'MAX' [Aggregation] function.
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of the [Expression] to get the maximum of and thereby of its [Max]
 */
class Max<E : Engine<E>, T : Comparable<T>?>(
    val expression: Expression<E, T?>,
) : Aggregation<E, T?> {
    override fun sql() = "MAX(${expression.sql()})"

    override fun defaultColumnName() = "MAX(${expression.defaultColumnName()})"

    override fun equals(other: Any?) = other is Max<E, T>
            && expression == other.expression

    override fun hashCode() = expression.hashCode()
}
