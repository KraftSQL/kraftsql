package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.objects.Row
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

open class SimulatorORMapping<E : Engine<E>> : ORMapping<E, List<Row>> {
    override fun getTypeFor(type: KType): Type<E> {
        throw NotImplementedError("Simulated Engines don't use a SQL Type system.")
    }

    override fun getKTypeFor(sqlType: Type<E>): KType {
        throw NotImplementedError("Simulated Engines don't use a SQL Type system.")
    }

    override fun <T : Any> deserializeQueryResult(queryResult: List<Row>, type: KClass<T>): List<T> {
        if (type == Row::class) {
            @Suppress("UNCHECKED_CAST")
            return queryResult as List<T>
        } else {
            return queryResult.map { row ->
                val constructor = type.primaryConstructor ?: throw IllegalStateException("No primary constructor for ${type.simpleName}.")
                constructor.callBy(constructor.parameters.associateWith { param -> row[param.name!!]})
            }
        }
    }
}
