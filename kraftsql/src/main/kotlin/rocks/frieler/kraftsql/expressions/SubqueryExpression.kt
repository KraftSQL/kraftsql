package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Engine

/**
 * Wrapper for [Select] statements which returns only a single value to use them as [Expression]s.
 *
 * Usually, SQL engines allow this out of the box, so no SQL code is necessary. However, for the type-safety of
 * **Kraft**SQL this wrapper is necessary.
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of the [Select]'s single result value
 */
class SubqueryExpression<E : Engine<E>, T>(
    val subquery: Select<E, *>,
) : Expression<E, T?> {
    override fun sql() = "(${subquery.sql()})"

    override fun equals(other: Any?) = other is SubqueryExpression<*, *> && subquery == other.subquery

    override fun hashCode() = subquery.hashCode()
}

/**
 * Convenience function to wrap a [Select] statement (which must return only a single value) in a [SubqueryExpression].
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of the [Select]'s single result value
 * @return a [SubqueryExpression] wrapping the [Select]
 */
fun <E : Engine<E>, T> Select<E, *>.asExpression() = SubqueryExpression<E, T>(this)
