package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * The `COUNT(...)` [Aggregation] that counts the number of rows, or non-NULL values.
 *
 * @param E the [Engine] that implements [Count] and for which the SQL code is rendered
 * @param expression the expression to count non-NULL values, or `null` to count all rows
 */
class Count<E : Engine<E>>(
    val expression: Expression<E, *>? = null
) : Aggregation<E, Long> {
    override fun sql() = "COUNT(${expression?.sql() ?: "*"})"

    override fun defaultColumnName() = "COUNT(${expression?.defaultColumnName() ?: "*"})"

    override fun equals(other: Any?) = other is Count<*>
        && expression == other.expression

    override fun hashCode() = Objects.hash(expression) + 1
}
