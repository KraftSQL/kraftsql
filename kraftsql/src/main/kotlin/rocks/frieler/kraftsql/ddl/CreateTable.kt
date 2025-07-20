package rocks.frieler.kraftsql.ddl

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.Table

class CreateTable<E : Engine<E>>(
    val table: Table<E, *>,
) : Command<E, Unit> {

    override fun sql() : String {
        return "CREATE TABLE ${table.sql()} (${table.columns.joinToString(", ") { it.sql() }});"
    }
}

fun <E : Engine<E>, T : Any> Table<E, T>.create(connection: Connection<E>) {
    connection.execute(CreateTable(this))
}
