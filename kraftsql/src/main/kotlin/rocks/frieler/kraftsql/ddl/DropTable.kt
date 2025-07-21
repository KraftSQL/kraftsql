package rocks.frieler.kraftsql.ddl

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.Table

class DropTable<E : Engine<E>>(
    val table: Table<E, *>,
    val ifExists: Boolean = false,
) : Command<E, Unit> {
    override fun sql() = "DROP TABLE${if (ifExists) " IF EXISTS" else ""} ${table.sql()}"
}

fun <E : Engine<E>> Table<E, *>.drop(connection: Connection<E>, ifExists: Boolean = false) {
    connection.execute(DropTable(this, ifExists))
}
