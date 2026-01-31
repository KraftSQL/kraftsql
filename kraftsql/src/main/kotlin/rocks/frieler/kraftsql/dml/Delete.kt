package rocks.frieler.kraftsql.dml

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Table

open class Delete<E : Engine<E>>(
    val table: Table<E, *>,
    val condition: Expression<E, Boolean?>? = null,
) : Command<E, Int> {
    override fun sql(): String = "DELETE FROM ${table.sql()}${condition?.let { " WHERE ${it.sql()}" } ?: ""}"
}

fun <E : Engine<E>> Delete<E>.execute(connection : Connection<E>) =
    connection.execute(this)
