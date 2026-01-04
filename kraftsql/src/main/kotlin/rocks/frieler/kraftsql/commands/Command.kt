package rocks.frieler.kraftsql.commands

import rocks.frieler.kraftsql.engine.Engine

/**
 * A SQL command that can be executed by an [Engine].
 *
 * @param E the [Engine] which can execute this [Command]
 * @param T the Kotlin type of the result when this [Command] is executed
 */
interface Command<E : Engine<E>, T> {
    fun sql(): String
}
