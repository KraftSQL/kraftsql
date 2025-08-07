package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.DefaultConnection
import rocks.frieler.kraftsql.engine.JdbcConnectionWrapper
import java.sql.DriverManager

class H2InMemoryConnection(
    database: String,
) : JdbcConnectionWrapper<H2Engine>(DriverManager.getConnection("jdbc:h2:mem:$database;DATABASE_TO_UPPER=FALSE")) {

    override val orm = H2ORMapping

    object Default : DefaultConnection<H2Engine>() {
        override fun instantiate(): H2InMemoryConnection {
            val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
            return H2InMemoryConnection(database)
        }
    }
}
