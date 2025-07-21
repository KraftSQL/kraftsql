package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.objects.Row
import rocks.frieler.kraftsql.dql.Select
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

open class JavaSqlConnectionWrapper<E : Engine<E>>(
    private val connection: java.sql.Connection,
) : Connection<E>, AutoCloseable by connection {
    override fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T> {
        val resultSet = connection.createStatement().executeQuery(select.sql())

        val result = mutableListOf<T>()
        while (resultSet.next()) {
            result.add(
                if (type != Row::class) {
                    val constructor = type.constructors.first()
                    constructor.callBy(constructor.parameters.associateWith { param ->
                        when (param.type) {
                            Integer::class.starProjectedType -> resultSet.getInt(param.name)
                            Long::class.starProjectedType -> resultSet.getLong(param.name)
                            String::class.starProjectedType -> resultSet.getString(param.name)
                            else -> throw NotImplementedError("Unsupported field type ${param.type}")
                        }
                    })
                } else {
                    @Suppress("UNCHECKED_CAST")
                    Row(
                        (1..resultSet.metaData.columnCount)
                            .map { resultSet.metaData.getColumnName(it) }
                            .associateWith { resultSet.getObject(it) }
                    ) as T
                }
            )
        }
        return result
    }

    override fun execute(createTable: CreateTable<E>) {
        connection.createStatement().execute(createTable.sql())
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        return connection.createStatement().executeUpdate(insertInto.sql())
    }
}
