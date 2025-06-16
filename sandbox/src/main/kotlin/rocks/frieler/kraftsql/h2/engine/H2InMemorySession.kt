package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.SqlConnectionSession
import java.sql.DriverManager

class H2InMemorySession(
    database: String,
) : SqlConnectionSession<H2Engine>(DriverManager.getConnection("jdbc:h2:mem:$database")) {
    object AutoInstance {
        private lateinit var instance : H2InMemorySession

        operator fun invoke(): H2InMemorySession {
            if (!::instance.isInitialized) {
                val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
                instance = H2InMemorySession(database)
            }

            return instance
        }
    }
}
