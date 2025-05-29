package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.queries.Select
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

// TODO: Should we separate "engine" and "connection"?
class H2InMemoryEngine(
    database: String,
) : Engine<H2InMemoryEngine>, AutoCloseable {
    private val connection: Connection = DriverManager.getConnection("jdbc:h2:mem:$database")

    @Suppress("kotlin:S6514")
    override fun close() {
        connection.close()
    }

    override fun <T : Any> execute(select: Select<H2InMemoryEngine, T>): ResultSet {
        return connection.createStatement().executeQuery(select.sql())
    }

    override fun execute(createTable: CreateTable<H2InMemoryEngine>) {
        connection.createStatement().execute(createTable.sql())
    }

    override fun execute(insertInto: InsertInto<H2InMemoryEngine, *>) : Int {
        return connection.createStatement().executeUpdate(insertInto.sql())
    }

    object AutoInstance {
        private lateinit var instance : H2InMemoryEngine;

        operator fun invoke(): H2InMemoryEngine {
            if (!::instance.isInitialized) {
                val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
                instance = H2InMemoryEngine(database)
            }

            return instance;
        }
    }
}
