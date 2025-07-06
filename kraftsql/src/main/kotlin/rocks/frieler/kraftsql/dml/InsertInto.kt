package rocks.frieler.kraftsql.dml

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Session
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.Table

class InsertInto<E : Engine<E>, T : Any>(
    val table: Table<E, T>,
    val values: Data<E, T>,
) : Command<E, Int> {

    override fun sql() : String {
        return "INSERT INTO ${table.sql()} ${values.sql()}"
    }
}

fun <E : Engine<E>, T : Any> Data<E, T>.insertInto(table: Table<E, T>, session: Session<E>) =
    session.execute(InsertInto(table, this))

fun <T : Any, E : Engine<E>> T.insertInto(table: Table<E, T>, session: Session<E>) =
    ConstantData<E, T>(this).insertInto(table, session)
