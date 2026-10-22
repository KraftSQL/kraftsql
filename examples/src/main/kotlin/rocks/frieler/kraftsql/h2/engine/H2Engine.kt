package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.data.Column
import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.data.Selection
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
        val (query, parameters) = query(data)
        val result = mutableListOf<DataRow>()
        @Suppress("SqlSourceToSinkFlow")
        connection.prepareStatement(query).use { statement ->
            parameters.forEachIndexed { index, (value, column) ->
                when (value) {
                    null -> statement.setNull(index + 1, column.type.native.jdbcType.vendorTypeNumber)
                    else -> statement.setObject(index + 1, value)
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

    /**
     * Generates the SQL query for the [Data], with its positional parameters and their [Column]s.
     */
    private fun query(data: Data<*>): Pair<String, List<Pair<Any?, Column>>> {
        return when (data) {
            is ConstantData<*> -> {
                val rows = data.items.map { if (it !is DataRow) DataClassSerDe.serialize(it) else it }
                val query = rows.joinToString(" UNION ALL ") {
                    data.schema.joinToString(
                        prefix = "SELECT ",
                        separator = ","
                    ) { column -> "CAST(? AS ${column.type.native.jdbcType.name}) AS `${column.name}`" }
                }
                query to rows.flatMap { row -> data.schema.map { column -> row[column.name] to column } }
            }
            is Selection -> {
                val (sourceQuery, sourceParameters) = query(data.source)
                val query = data.schema.joinToString(
                    prefix = "SELECT ",
                    separator = ",",
                    postfix = " FROM ($sourceQuery) AS `source`"
                ) { column -> "`${column.name}`" }
                query to sourceParameters
            }
            else -> throw NotImplementedError("Collecting $data is not implemented.")
        }
    }
}
