package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

interface Expression<E: Engine<E>, T> {
    fun sql(): String
}
