package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

interface Queryable<E : Engine<E>> {
    val connection: Connection<E>

    fun sql(): String
}
