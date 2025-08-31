package rocks.frieler.kraftsql.dml

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

class CommitTransaction<E : Engine<E>> : Command<E, Unit> {
    override fun sql(): String = "COMMIT"
}

fun <E : Engine<E>> CommitTransaction<E>.execute(connection: Connection<E>) {
    connection.execute(this)
}
