package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.queries.Select
import java.sql.Connection
import java.sql.ResultSet

open class SqlConnectionSession<E : Engine<E>>(
    private val connection: Connection,
) : Session<E>, AutoCloseable by connection {
    override fun <T : Any> execute(select: Select<E, T>): ResultSet {
        return connection.createStatement().executeQuery(select.sql())
    }

    override fun execute(createTable: CreateTable<E>) {
        connection.createStatement().execute(createTable.sql())
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        return connection.createStatement().executeUpdate(insertInto.sql())
    }
}
