package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.queries.Select
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class H2InMemoryConnection(
    database: String,
) : rocks.frieler.kraftsql.engine.Connection<H2Engine>, AutoCloseable {
    private val connection: Connection = DriverManager.getConnection("jdbc:h2:mem:$database")

    @Suppress("kotlin:S6514")
    override fun close() {
        connection.close()
    }

    override fun <T : Any> execute(select: Select<H2Engine, T>): ResultSet {
        return connection.createStatement().executeQuery(select.sql())
    }

    override fun execute(createTable: CreateTable<H2Engine>) {
        connection.createStatement().execute(createTable.sql())
    }

    override fun execute(insertInto: InsertInto<H2Engine, *>) : Int {
        return connection.createStatement().executeUpdate(insertInto.sql())
    }

    object AutoInstance {
        private lateinit var instance : H2InMemoryConnection

        operator fun invoke(): H2InMemoryConnection {
            if (!::instance.isInitialized) {
                val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
                instance = H2InMemoryConnection(database)
            }

            return instance
        }
    }
}
