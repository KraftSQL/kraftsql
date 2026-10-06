package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.data.Type
import rocks.frieler.kraftsql.engine.DataClassSerDe
import rocks.frieler.kraftsql.engine.Engine
import java.sql.DriverManager
import java.sql.JDBCType
import kotlin.reflect.KClass

/**
 * [Engine] implementation for the [h2 database](https://h2database.com/html/main.html) for experimentation and demo
 * purposes.
 *
 * KraftSQL's [rocks.frieler.kraftsql.data.Type]s are mapped to H2's native types as follows:
 * - [Int][rocks.frieler.kraftsql.data.Type.INTEGER] to `INTEGER`
 * - [String][rocks.frieler.kraftsql.data.Type.STRING] to `VARCHAR` (without a length limit)
 */
class H2Engine : Engine {
    // TODO: Derive database name from environment variables
    private val connection = DriverManager.getConnection("jdbc:h2:mem:examples;DATABASE_TO_UPPER=FALSE")

    private class NativeType(val jdbcType: JDBCType, val javaClass: Class<*>)

    private val Type.native get() = when (this) {
        Type.INTEGER, Type.NULLABLE_INTEGER -> NativeType(JDBCType.INTEGER, Int::class.javaObjectType)
        Type.STRING, Type.NULLABLE_STRING -> NativeType(JDBCType.VARCHAR, String::class.java)
        else -> throw IllegalStateException("Unsupported type $this")
    }

    override fun <T : Any> collect(data: Data<T>, type: KClass<T>): List<T> {
        when (data) {
            is ConstantData<T> -> {
                val rows = data.items.map { if (it !is DataRow) DataClassSerDe.serialize(it) else it }
                val result = mutableListOf<DataRow>()
                val query = rows.joinToString(" UNION ALL ") {
                    data.schema.joinToString(
                        prefix = "SELECT ",
                        separator = ","
                    ) { column -> "CAST(? AS ${column.type.native.jdbcType.name}) AS `${column.name}`" }
                }
                @Suppress("SqlSourceToSinkFlow")
                connection.prepareStatement(query).use { statement ->
                    var parameterIndex = 1
                    for (row in rows) {
                        for (column in data.schema) {
                            when (val value = row[column.name]) {
                                null -> statement.setNull(parameterIndex++, column.type.native.jdbcType.vendorTypeNumber)
                                else -> statement.setObject(parameterIndex++, value)
                            }
                        }
                    }
                    val resultSet = statement.executeQuery()
                    // TODO: Encapsulate handling of JDBC ResultSet.
                    while (resultSet.next()) {
                        result.add(DataRow(data.schema.map { column ->
                            column.name to resultSet.getObject(column.name, column.type.native.javaClass)
                        }))
                    }
                }
                return result
                    .map { @Suppress("UNCHECKED_CAST") if (type != DataRow::class) DataClassSerDe.deserialize(it, type) else it as T }
                    .toList()
            }
            else -> throw NotImplementedError("Collecting $data is not implemented.")
        }
    }
}
