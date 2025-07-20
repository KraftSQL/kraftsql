package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.DefaultConnection
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.JavaSqlConnectionWrapper
import java.sql.DriverManager

class H2InMemoryConnection(
    database: String,
) : JavaSqlConnectionWrapper<H2Engine>(DriverManager.getConnection("jdbc:h2:mem:$database;DATABASE_TO_UPPER=FALSE")) {
    object Default : DefaultConnection<H2Engine>() {
        override fun instantiate(): Connection<H2Engine> {
            val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
            return H2InMemoryConnection(database)
        }
    }
}
