package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.models.Model
import rocks.frieler.kraftsql.models.Row
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

open class Select<E : Engine<E>, T : Any>(
    val from: Model<E, T>,
    val columns: List<ColumnExpression<E, *>>? = null,
) : Model<E, T>(from.engine) {

    override fun sql() =
        "SELECT ${columns?.joinToString(", ") { it.sql() } ?: "*"} FROM ${from.sql()}"

    fun execute(type: KClass<T>): List<T> {
        val resultSet = engine.execute(this)

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

    companion object {
        operator fun <E: Engine<E>> invoke(from: Model<E, *>, columns: List<ColumnExpression<E, *>>? = null) = Select(from.asRows(), columns)
    }
}

inline fun <reified T : Any> Select<*, T>.execute() = execute(T::class)
