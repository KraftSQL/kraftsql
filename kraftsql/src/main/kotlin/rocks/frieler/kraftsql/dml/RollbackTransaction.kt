package rocks.frieler.kraftsql.dml

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine

class RollbackTransaction<E : Engine<E>> : Command<E, Unit> {
    override fun sql(): String = "ROLLBACK"
}

fun <E : Engine<E>> RollbackTransaction<E>.execute(connection: Connection<E>) {
    connection.execute(this)
}
