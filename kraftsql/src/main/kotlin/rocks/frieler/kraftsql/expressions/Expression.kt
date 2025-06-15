package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

interface Expression<E: Engine<E>, T> {
    fun sql(): String

    fun defaultColumnName(): String

    override fun equals(other: Any?) : Boolean

    override fun hashCode(): Int
}
