package rocks.frieler.kraftsql.ddl

import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.Table

class CreateTable<E : Engine<E>>(
    private val connection: Connection<E>,
    private val table: Table<E, *>,
) {
    fun sql() : String {
        return "CREATE TABLE ${table.sql()} (${table.columns.joinToString(", ") { it.sql() }});"
    }

    fun execute() {
        connection.execute(this)
    }
}

fun <E : Engine<E>, T : Any> Table<E, T>.create(connection: Connection<E>) {
    CreateTable(connection, this).execute()
}
