package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.time.Instant
import java.time.LocalDate

/**
 * [Expression] that implements a constant value in SQL.
 *
 * @param E the [Engine] that implements this [Constant] and for which the SQL code is rendered
 * @param T the Kotlin type of the [Constant] value
 * @param value the constant value
 */
open class Constant<E : Engine<E>, T>(
    val value: T,
) : Expression<E, T> {
    override val subexpressions = emptyList<Expression<E, *>>()

    override fun sql(): String {
        return when (value) {
            null -> "NULL"
            is Boolean -> if (value) "TRUE" else "FALSE"
            is Number -> value.toString()
            is Instant -> "TIMESTAMP '$value'"
            is LocalDate -> "DATE '$value'"
            else -> "'$value'"
        }
    }

    override fun defaultColumnName() = sql()

    override fun equals(other: Any?) = other is Constant<E, T> && value == other.value

    override fun hashCode() = value.hashCode()
}
