package rocks.frieler.kraftsql.commands

import rocks.frieler.kraftsql.engine.Engine

interface Command<E : Engine<E>, T> {
    fun sql(): String
}
