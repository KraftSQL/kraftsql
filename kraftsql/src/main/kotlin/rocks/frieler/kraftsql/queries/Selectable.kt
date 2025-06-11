package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Engine

interface Selectable<E : Engine<E>> {
    fun sql(): String
}
