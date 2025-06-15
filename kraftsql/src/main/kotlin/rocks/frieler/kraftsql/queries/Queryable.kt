package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Engine

interface Queryable<E : Engine<E>> {
    fun sql(): String
}
