package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.DataRow
import java.sql.ResultSet
import java.sql.ResultSetMetaData
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
                    type == String::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getString(columnOffset + 1) as T
                    }
                    type == DataRow::class -> {
                        @Suppress("UNCHECKED_CAST")
                        DataRow(
                            (columnOffset + 1..queryResult.metaData.columnCount)
                                .map { queryResult.metaData.getColumnLabel(it) to queryResult.metaData.getColumnTypeName(it) }
                                .associate { (name, sqlType) -> name to
                                    when (val value = queryResult.getObject(name)) {
                                        is java.sql.Array -> {
                                            val elements = (value.array as Array<*>)
                                            val elementType = getKTypeFor(types.parseType(sqlType)).arguments.single().type!!.jvmErasure.java
                                            @Suppress("UNCHECKED_CAST")
                                            (java.lang.reflect.Array.newInstance(elementType, elements.size) as Array<Any?>).also { array ->
                                                elements.copyInto(array)
                                            }
                                        }
                                        is ResultSet -> deserializeQueryResult(value, DataRow::class)
                                        else -> value
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
