package rocks.frieler.kraftsql.dml

import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.models.Model
import rocks.frieler.kraftsql.objects.Table

class InsertInto<E : Engine<E>, T : Any>(
    private val connection: Connection<E>,
    private val table: Table<E, T>,
    private val values: Model<E, T>,
) {
    fun sql() : String {
        return "INSERT INTO ${table.sql()} ${values.sql()}"
    }

    fun execute() =
        connection.execute(this)
}

fun <E : Engine<E>, T : Any> Model<E, T>.insertInto(table: Table<E, T>, connection: Connection<E>) =
    InsertInto(connection, table, this).execute()
