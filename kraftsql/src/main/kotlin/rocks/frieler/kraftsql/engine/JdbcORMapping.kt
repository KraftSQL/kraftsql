package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.objects.Row
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

abstract class JdbcORMapping<E : JdbcEngine<E>>(
    protected val types: Types<E>,
): ORMapping<E, ResultSet> {

    override fun <T : Any> deserializeQueryResult(queryResult: ResultSet, type: KClass<T>): List<T> {
        val result = mutableListOf<T>()
        while (queryResult.next()) {
            result.add(
                when (type) {
                    Integer::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getInt(2) as T
                    }
                    Long::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getLong(2) as T
                    }
                    String::class -> {
                        @Suppress("UNCHECKED_CAST")
                        queryResult.getString(2) as T
                    }
                    Row::class -> {
                        @Suppress("UNCHECKED_CAST")
                        Row(
                            (1..queryResult.metaData.columnCount)
                                .map { queryResult.metaData.getColumnName(it) to queryResult.metaData.getColumnTypeName(it) }
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
                                        else -> value
                                    }
                                }
                        ) as T
                    }
                    else -> {
                        val constructor = type.constructors.first()
                        constructor.callBy(constructor.parameters.associateWith { param ->
                            when (param.type.jvmErasure.starProjectedType) {
                                Integer::class.starProjectedType -> queryResult.getInt(param.name)
                                Long::class.starProjectedType -> queryResult.getLong(param.name)
                                String::class.starProjectedType -> queryResult.getString(param.name)
                                Array::class.starProjectedType -> {
                                    val elementType = param.type.arguments.single().type!!.jvmErasure
                                    val elements = deserializeQueryResult(queryResult.getArray(param.name).resultSet, elementType)
                                    @Suppress("UNCHECKED_CAST")
                                    val array = java.lang.reflect.Array.newInstance(elementType.java, elements.size) as Array<Any?>
                                    elements.forEachIndexed { index, element -> array[index] = element }
                                    return@associateWith array
                                }
                                else -> throw NotImplementedError("Unsupported field type ${param.type}")
                            }
                        })
                    }
                }
            )
        }
        return result
    }
}
