package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class ConstantExpression<E : Engine<E>, T : Any?>(
    private val value: T,
) : Expression<E, T> {
    override fun sql(): String {
        return when (value) {
            null -> "NULL"
            is Number -> value.toString()
            else -> "'$value'"
        }
    }
}
