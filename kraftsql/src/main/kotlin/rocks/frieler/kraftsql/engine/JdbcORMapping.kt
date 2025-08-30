package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

abstract class JdbcORMapping<E : JdbcEngine<E>>(
    protected val types: Types<E>,
): ORMapping<E, ResultSet> {

    override fun <T : Any> deserializeQueryResult(queryResult: ResultSet, type: KClass<T>): List<T> {
        return deserializeQueryResultInternal(queryResult, type)
    }

    private fun <T : Any> deserializeQueryResultInternal(queryResult: ResultSet, type: KClass<T>, columnOffset: Int = 0): MutableList<T> {
        val result = mutableListOf<T>()
        while (queryResult.next()) {
            result.add(
                when {
                    type == Integer::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getInt(columnOffset + 1) as T
                    }
                    type == Long::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getLong(columnOffset + 1) as T
                    }
                    type == Float::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getFloat(columnOffset + 1) as T
                    }
                    type == Double::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getDouble(columnOffset + 1) as T
                    }
                    type == BigDecimal::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getBigDecimal(columnOffset + 1).stripTrailingZeros() as T
                    }
                    type == String::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getString(columnOffset + 1) as T
                    }
                    type == Instant::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getTimestamp(columnOffset + 1).toInstant() as T
                    }
                    type == LocalDate::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getDate(columnOffset + 1).toLocalDate() as T
                    }
                    type.starProjectedType == typeOf<Array<*>>() -> {
                        val jdbcArray = queryResult.getArray(columnOffset + 1)
                        val elementType = getKTypeFor(types.parseType(jdbcArray.baseTypeName)).arguments.single().type!!
                        val elements = deserializeQueryResultInternal(jdbcArray.resultSet, elementType.jvmErasure, 1)
                        val array = java.lang.reflect.Array.newInstance(elementType.jvmErasure.java, elements.size)
                        @Suppress("UNCHECKED_CAST")
                        elements.forEachIndexed { index, element -> (array as Array<Any?>)[index] = element }
                        @Suppress("UNCHECKED_CAST")
                        array as T
                    }
                    type == DataRow::class -> {
                        @Suppress("UNCHECKED_CAST")
                        DataRow(
                            (columnOffset + 1..queryResult.metaData.columnCount)
                                .map { queryResult.metaData.getColumnLabel(it) to getKTypeFor(types.parseType(queryResult.metaData.getColumnTypeName(it))) }
                                .associate { (name, valueColumnType) -> name to
                                    when {
                                        valueColumnType == typeOf<Int>() -> queryResult.getInt(name)
                                        valueColumnType == typeOf<Long>() -> queryResult.getLong(name)
                                        valueColumnType == typeOf<String>() -> queryResult.getString(name)
                                        valueColumnType == typeOf<LocalDate>() -> queryResult.getDate(name).toLocalDate()
                                        valueColumnType.jvmErasure.starProjectedType == typeOf<Array<*>>() -> {
                                            val elementType = valueColumnType.arguments.single().type!!.jvmErasure
                                            val elements = deserializeQueryResultInternal(queryResult.getArray(name).resultSet, elementType, 1)
                                            java.lang.reflect.Array.newInstance(elementType.java, elements.size).also { array ->
                                                elements.forEachIndexed { index, element -> (array as Array<Any?>)[index] = element }
                                            }
                                        }
                                        valueColumnType == typeOf<DataRow>() -> deserializeQueryResultInternal(queryResult.getObject(name) as ResultSet, DataRow::class)
                                        else -> throw IllegalArgumentException("Unsupported value type $valueColumnType.")
                                    }
                                }
                        ) as T
                    }
                    type.isData -> {
                        val resultSchema = queryResult.metaData.getSchema()
                        val constructor = type.ensuredPrimaryConstructor()
                        constructor.callBy(constructor.parameters.associateWith { param ->
                            when {
                                param.type == typeOf<Integer>() -> {
                                    if (resultSchema.any { it.name == param.name })
                                        queryResult.getInt(param.name)
                                    else
                                        queryResult.getInt(columnOffset + param.index + 1)
                                }
                                param.type == typeOf<Long>() -> {
                                    if (resultSchema.any { it.name == param.name })
                                        queryResult.getLong(param.name)
                                    else
                                        queryResult.getLong(columnOffset + param.index + 1)
                                }
                                param.type == typeOf<String>() -> {
                                    if (resultSchema.any { it.name == param.name })
                                        queryResult.getString(param.name)
                                    else
                                        queryResult.getString(columnOffset + param.index + 1)
                                }
                                param.type == typeOf<LocalDate>() -> {
                                    if (resultSchema.any { it.name == param.name })
                                        queryResult.getDate(param.name).toLocalDate()
                                    else
                                        queryResult.getDate(columnOffset + param.index + 1).toLocalDate()
                                }
                                param.type.jvmErasure.starProjectedType == typeOf<Array<*>>() -> {
                                    val elementType = param.type.arguments.single().type!!.jvmErasure
                                    val jdbcArray =
                                        if (resultSchema.any { it.name == param.name })
                                            queryResult.getArray(param.name)
                                        else
                                            queryResult.getArray(columnOffset + param.index + 1)
                                    val elements = deserializeQueryResultInternal(jdbcArray.resultSet, elementType, 1)
                                    @Suppress("UNCHECKED_CAST")
                                    val array = java.lang.reflect.Array.newInstance(elementType.java, elements.size) as Array<Any?>
                                    elements.forEachIndexed { index, element -> array[index] = element }
                                    return@associateWith array
                                }
                                else -> {
                                    val jdbcRowValue = if (resultSchema.any { it.name == param.name })
                                        queryResult.getObject(param.name, ResultSet::class.java)
                                    else
                                        queryResult.getObject(columnOffset + param.index + 1, ResultSet::class.java)
                                    deserializeQueryResultInternal(jdbcRowValue, param.type.jvmErasure).single()
                                }
                            }
                        })
                    }
                    else -> throw IllegalArgumentException("Unsupported target type ${type.qualifiedName}.")
                }
            )
        }
        return result
    }

    private fun ResultSetMetaData.getSchema() : List<Column<E>> =
        (1..columnCount).map { index ->
            Column(getColumnLabel(index), types.parseType(getColumnTypeName(index)))
        }
}
