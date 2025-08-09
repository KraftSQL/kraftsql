package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.Row
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

interface ORMapping<E : Engine<E>, R : Any> {
    fun getTypeFor(type: KType): Type<E>

    fun getKTypeFor(sqlType: Type<E>): KType

    fun getSchemaFor(type:  KClass<*>): List<Column<E>> =
        type.memberProperties.map { field ->
            Column(field.name, getTypeFor(field.returnType))
        }

    fun <T : Any> serialize(value: T): Map<String, Constant<E, *>> =
        if (value is Row) {
            value.values
        } else {
            @Suppress("UNCHECKED_CAST")
            (value::class as KClass<T>).memberProperties.associate { it.name to it.get(value) }
        }
            .mapValues { (_, value) -> Constant(value) }

    fun <T : Any> deserializeQueryResult(queryResult : R, type: KClass<T>): List<T>
}
