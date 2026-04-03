package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * SQL 'MIN' [Aggregation] function.
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of the [Expression] to get the minimum of and thereby of its [Min]
 */
class Min<E : Engine<E>, T : Comparable<T>?>(
    val expression: Expression<E, T?>,
) : Aggregation<E, T?> {
    override fun sql() = "MIN(${expression.sql()})"

    override fun defaultColumnName() = "MIN(${expression.defaultColumnName()})"

    override fun equals(other: Any?) = other is Min<E, T>
            && expression == other.expression

    override fun hashCode() = expression.hashCode()
}
