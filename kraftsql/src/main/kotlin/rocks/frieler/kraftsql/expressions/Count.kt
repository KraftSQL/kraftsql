package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Count<E : Engine<E>> : Aggregation<E, Long> {
    override fun sql() = "COUNT(*)"

    override fun defaultColumnName() = "COUNT(*)"

    override fun equals(other: Any?) = other is Count<*>

    override fun hashCode() = 1
}
