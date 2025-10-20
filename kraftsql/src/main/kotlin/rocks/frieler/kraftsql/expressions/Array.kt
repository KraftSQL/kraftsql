package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * The SQL `Array(...)` [Expression] that creates an array from the given sub-[Expression].
 *
 * @param <E> the [Engine] that implements [Array] and for which the SQL code is rendered
 * @param <T> the Kotlin type of the array's elements
 * @param elements the sub-[Expression]s to create the array from
 */
class Array<E : Engine<E>, T : Any>(
    val elements: kotlin.Array<Expression<E, out T>>?,
) : Expression<E, kotlin.Array<T?>> {
    override fun sql(): String {
        if (elements == null) {
            return "NULL"
        }
        return "ARRAY [${elements.joinToString(",") { it.sql() }}]"
    }

    override fun defaultColumnName(): String {
        if (elements == null) {
            return "NULL"
        }
        return "[${elements.joinToString(",") { it.defaultColumnName() }}]"
    }

    override fun equals(other: Any?) = other is Array<E, *> && elements.contentEquals(other.elements)

    override fun hashCode() = elements.hashCode()

    companion object {
        /**
         * Creates an [Array] from the given [Expression]s.
         *
         * @param <E> the [Engine] that implements [Array] and for which the SQL code is rendered
         * @param <T> the Kotlin type of the array's elements
         * @param elements the sub-[Expression]s to create the array from
         */
        operator fun <E : Engine<E>, T : Any> invoke(vararg elements: Expression<E, out T>) = Array(elements.toList().toTypedArray())
    }
}
