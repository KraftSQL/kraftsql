package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.JdbcConnectionWrapper
import java.sql.DriverManager

class H2InMemoryConnection(
    database: String,
) : JdbcConnectionWrapper<H2Engine>(DriverManager.getConnection("jdbc:h2:mem:$database;DATABASE_TO_UPPER=FALSE")) {

    override val orm = H2ORMapping
}
