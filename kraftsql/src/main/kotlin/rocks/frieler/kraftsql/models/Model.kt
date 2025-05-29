package rocks.frieler.kraftsql.models

import rocks.frieler.kraftsql.engine.Engine

abstract class Model<E : Engine<E>, T : Any>(
    val engine: E,
) {
    @Suppress("UNCHECKED_CAST")
    fun asRows() = this as Model<E, Row>

    abstract fun sql(): String
}
