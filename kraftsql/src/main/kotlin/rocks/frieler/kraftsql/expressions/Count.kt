package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * The `COUNT()` [Aggregation] that counts the number of rows.
 *
 * @param <E> the [Engine] that implements [Count] and for which the SQL code is rendered
 */
class Count<E : Engine<E>> : Aggregation<E, Long?> {
    override fun sql() = "COUNT(*)"

    override fun defaultColumnName() = "COUNT(*)"

    override fun equals(other: Any?) = other is Count<*>

    override fun hashCode() = 1
}
