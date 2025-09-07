package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

open class SimulatorORMapping<E : Engine<E>> : ORMapping<E, List<DataRow>> {
    override fun getTypeFor(type: KType): Type<E> {
        throw NotImplementedError("Simulated Engines don't use a SQL Type system.")
    }

    override fun getKTypeFor(sqlType: Type<E>): KType {
        throw NotImplementedError("Simulated Engines don't use a SQL Type system.")
    }

    override fun <T : Any> deserializeQueryResult(queryResult: List<DataRow>, type: KClass<T>): List<T> {
        if (type == DataRow::class) {
            @Suppress("UNCHECKED_CAST")
            return queryResult as List<T>
        } else {
            return queryResult.map { row -> deserializeValue(row, type) as T }
        }
    }

    private fun <T : Any> deserializeValue(value: Any?, type: KClass<T>): T? =
        when {
            value == null || type.isInstance(value) && type.starProjectedType != typeOf<Array<*>>() -> {
                @Suppress("UNCHECKED_CAST")
                value as? T
            }
            value is Array<*> && type.starProjectedType == typeOf<Array<*>>() -> {
                val array = java.lang.reflect.Array.newInstance(type.java.componentType, value.size)
                value.forEachIndexed { index, element ->
                    @Suppress("UNCHECKED_CAST")
                    (array as Array<Any?>)[index] = deserializeValue(element, type.java.componentType.kotlin)
                }
                @Suppress("UNCHECKED_CAST")
                array as T
            }
            value is DataRow && type.isData -> {
                val constructor = type.primaryConstructor ?: throw IllegalStateException("No primary constructor for ${type.simpleName}.")
                constructor.callBy(constructor.parameters.associateWith { param -> deserializeValue(value[param.name!!], param.type.jvmErasure) })
            }
            else -> throw IllegalStateException("Cannot deserialize $value to $type.")
        }
}
