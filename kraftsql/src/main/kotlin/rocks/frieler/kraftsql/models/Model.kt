package rocks.frieler.kraftsql.models

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.queries.Queryable

abstract class Model<E : Engine<E>, T : Any>() : Queryable<E>, HasColumns<E, T> {
    @Suppress("UNCHECKED_CAST")
    fun asRows() = this as Model<E, Row>

    abstract override fun sql(): String
}
