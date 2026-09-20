package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.data.Column
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.data.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Utility object to serialize into and deserialize from [DataRow]s.
 */
object DataClassSerDe {
    fun getSchemaFor(kClass: KClass<*>): List<Column> {
        require(kClass.isData) { "${kClass.qualifiedName} is not a data class." }
        return kClass.ensuredPrimaryConstructor().parameters.map { Column(it.name!!, Type.get(it.type)) }
    }

    fun <T : Any> serialize(item: T): DataRow {
        require(item::class.isData) { "${item::class.qualifiedName} is not a data class." }
        val fields = item::class.ensuredPrimaryConstructor()
            .parameters.map { param ->
                @Suppress("UNCHECKED_CAST") (item::class as KClass<T>).memberProperties.single { it.name == param.name!! }
            }
        return DataRow(fields.map { field -> Pair(field.name, field.get(item)) })
    }

    fun <T : Any> deserialize(row: DataRow, type: KClass<T>): T {
        require(type.isData) { "${type.qualifiedName} is not a data class." }
        val constructor = type.ensuredPrimaryConstructor()
        return constructor.callBy(constructor.parameters.associateWith { param -> row[param.name!!] })
    }

    private fun <T : Any> KClass<T>.ensuredPrimaryConstructor() =
        primaryConstructor ?: throw IllegalStateException("$qualifiedName is lacking a primary constructor.")
}
