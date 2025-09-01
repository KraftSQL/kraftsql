package rocks.frieler.kraftsql.dml

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

class BeginTransaction<E : Engine<E>> : Command<E, Unit> {
    override fun sql(): String = "BEGIN TRANSACTION"
}

fun <E : Engine<E>> BeginTransaction<E>.execute(connection: Connection<E>) {
    connection.execute(this)
}
