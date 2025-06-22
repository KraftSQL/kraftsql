package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.DefaultSession
import rocks.frieler.kraftsql.engine.Session
import rocks.frieler.kraftsql.engine.SqlConnectionSession
import java.sql.DriverManager

class H2InMemorySession(
    database: String,
) : SqlConnectionSession<H2Engine>(DriverManager.getConnection("jdbc:h2:mem:$database")) {
    object Default : DefaultSession<H2Engine>() {
        override fun instantiate(): Session<H2Engine> {
            val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
            return H2InMemorySession(database)
        }
    }
}
