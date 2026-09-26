package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.engine.DataClassSerDe
import rocks.frieler.kraftsql.engine.Engine
import java.sql.DriverManager
import kotlin.reflect.KClass

/**
 * [Engine] implementation for the [h2 database](https://h2database.com/html/main.html) for experimentation and demo
 * purposes.
 */
class H2Engine : Engine {
    // TODO: Derive database name from environment variables
    private val connection = DriverManager.getConnection("jdbc:h2:mem:examples;DATABASE_TO_UPPER=FALSE")

    override fun <T : Any> collect(data: Data<T>, type: KClass<T>): List<T> {
        when (data) {
            is ConstantData<T> -> {
                val rows = data.items.map { if (it !is DataRow) DataClassSerDe.serialize(it) else it }
                val result = mutableListOf<DataRow>()
                connection.createStatement().use { statement ->
                    @Suppress("SqlSourceToSinkFlow")
                    val resultSet = statement.executeQuery(rows.joinToString(" UNION ALL ") { row ->
                        row.entries.joinToString(
                            prefix = "SELECT ",
                            separator = ","
                        ) { "${it.second} AS `${it.first}`" } // TODO: generate proper SQL for values.
                    })
                    // TODO: Encapsulate handling of JDBC ResultSet.
                    val fields = mutableListOf<String>().apply {
                        resultSet.metaData.run {
                            for (index in 1..columnCount) {
                                add(getColumnName(index))
                            }
                        }
                    }
                    while (resultSet.next()) {
                        result.add(DataRow(fields.map { field ->
                            field to resultSet.getInt(field) // TODO: Obey type, watch for null, ...
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
