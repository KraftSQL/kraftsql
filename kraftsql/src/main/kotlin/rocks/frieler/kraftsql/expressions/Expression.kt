package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * An SQL expression.
 *
 * @param <E> the [Engine] that implements and executes this [Expression]
 * @param <T> the Kotlin type of the [Expression]'s value
 */
interface Expression<E: Engine<E>, out T> {
    /**
     * Generates the SQL code for this [Expression].
     *
     * @return the SQL code for this [Expression]
     */
    fun sql(): String

    /**
     * Returns the name of the resulting column, when this [Expression] is `SELECT`ed without an alias.
     *
     * @return the default column name  when `SELECT`ing this [Expression] without an alias
     */
    fun defaultColumnName(): String

    override fun equals(other: Any?) : Boolean

    override fun hashCode(): Int
}

/**
 * Forcefully marks an [Expression] as not-nullable.
 *
 * Most [Expression]s are nullable, because SQL tends to return `NULL` in many error cases. However, sometimes the
 * developer knows (and has verified by tests) that an [Expression] will never be `NULL`.
 *
 * @param <E> the [Engine] that implements and executes this [Expression]
 * @param <T> the non-nullable Kotlin type of the [Expression]'s value
 * @return the same [Expression] as this one, but not-nullable
 */
@Suppress("UNCHECKED_CAST")
fun <E: Engine<E>, T : Any> Expression<E, T?>.knownNotNull() = this as Expression<E, T>
