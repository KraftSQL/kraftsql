package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.models.Model
import rocks.frieler.kraftsql.models.Row
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

open class Select<E : Engine<E>, T : Any>(
    val source: Queryable<E>,
    val joins: List<Join<E>> = emptyList(),
    val columns: List<ColumnExpression<E, *>>? = null,
    val filter: Expression<E, Boolean>? = null,
) : Model<E, T>(source.connection) {

    init {
        require(joins.all { it.data.connection == source.connection }) { "cannot join data from different connections" }
    }

    override fun sql() = """
        SELECT ${columns?.joinToString(", ") { it.sql() } ?: "*"}
        FROM ${source.sql()}
        ${joins.joinToString("\n") { it.sql() }}
        ${if (filter != null) "WHERE ${filter.sql()}" else ""}
    """.trimIndent()

    fun execute(type: KClass<T>): List<T> {
        val resultSet = connection.execute(this)

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
}

inline fun <reified T : Any> Select<*, T>.execute() = execute(T::class)
