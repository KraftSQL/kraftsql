package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.JdbcEngine

object H2Engine : JdbcEngine<H2Engine>() {
    object DefaultConnection : rocks.frieler.kraftsql.engine.DefaultConnection<H2Engine, Connection<H2Engine>>() {
        override fun instantiate(): H2InMemoryConnection {
            val database = System.getenv("H2_DATABASE_NAME") ?: throw IllegalStateException("H2_DATABASE_NAME is not set")
            return H2InMemoryConnection(database)
        }
    }
}
