package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression

/**
 * Projection of an [Expression] as column in a [Select] statement.
 *
 * @param E the [Engine] that implements this [Projection]
 * @param T the Kotlin type of the [Expression]'s value
 * @param value the [Expression] to project
 * @param alias an optional alias for the resulting column
 */
class Projection<E : Engine<E>, T>(
    val value: Expression<E, T>,
    val alias: String? = null,
) {
    fun sql() = value.sql() + if (alias != null) " AS `$alias`" else ""
}
