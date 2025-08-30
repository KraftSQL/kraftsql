package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dql.Select
import kotlin.reflect.KClass

abstract class JdbcConnectionWrapper<E : JdbcEngine<E>>(
    private val connection: java.sql.Connection,
) : Connection<E>, AutoCloseable by connection {
    abstract val orm: JdbcORMapping<E>

    override fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T> {
        val resultSet = connection.createStatement().executeQuery(select.sql())
        return orm.deserializeQueryResult(resultSet, type)
    }

    override fun execute(createTable: CreateTable<E>) {
        connection.createStatement().execute(createTable.sql())
    }

    override fun execute(dropTable: DropTable<E>) {
        connection.createStatement().execute(dropTable.sql())
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        return connection.createStatement().executeUpdate(insertInto.sql())
    }

    override fun execute(delete: Delete<E>): Int {
        return connection.createStatement().executeUpdate(delete.sql())
    }
}
