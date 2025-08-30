package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.time.Instant
import java.time.LocalDate

open class Constant<E : Engine<E>, T : Any>(
    val value: T?,
) : Expression<E, T> {
    override fun sql(): String {
        return when (value) {
            null -> "NULL"
            is Number -> value.toString()
            is Instant -> "TIMESTAMP '$value'"
            is LocalDate -> "DATE '$value'"
            else -> "'$value'"
        }
    }

    override fun equals(other: Any?) = other is Constant<E, T> && value == other.value

    override fun hashCode() = value.hashCode()

    override fun defaultColumnName() = sql()
}
