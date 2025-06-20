package rocks.frieler.kraftsql.ddl

import rocks.frieler.kraftsql.engine.Session
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.Table

class CreateTable<E : Engine<E>>(
    private val session: Session<E>,
    private val table: Table<E, *>,
) {
    fun sql() : String {
        return "CREATE TABLE ${table.sql()} (${table.columns.joinToString(", ") { it.sql() }});"
    }

    fun execute() {
        session.execute(this)
    }
}

fun <E : Engine<E>, T : Any> Table<E, T>.create(session: Session<E>) {
    CreateTable(session, this).execute()
}
