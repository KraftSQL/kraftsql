package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.DataRow
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Base interface for mapping between Kotlin and SQL type-system.
 *
 * @param <E> the SQL [Engine] to map to and from
 * @param <R> the result-type of a query from a connection to that [Engine]
 */
interface ORMapping<E : Engine<E>, R : Any> {
    fun getTypeFor(type: KType): Type<E, *>

    fun getSchemaFor(type: KClass<*>): List<Column<E>> {
        check(type.isData) { "Automatic schema generation for non-data class ${type.qualifiedName} is not supported." }

        return type.ensuredPrimaryConstructor().parameters
            .map { parameter -> Column(parameter.name!!, getTypeFor(parameter.type), parameter.type.isMarkedNullable) }
    }

    /**
     * Serializes a value into an [Expression] to be used in an SQL statement.
     *
     * The [Expression] is constant, which doesn't necessarily mean only [Constant]s, but an [Expression] that is built
     * up on [Constant]s.
     *
     * @param T the value's and [Expression]'s type
     * @param value the value to serialize
     * @return an [Expression] that constantly evaluates to the SQL equivalent of the value
     */
    fun <T : Any> serialize(value: T?): Expression<E, out T?> =
        when {
            value == null -> Constant(null)
            value is Boolean -> Constant(value)
            value is Number -> Constant(value)
            value is String -> Constant(value)
            value is Instant -> Constant(value)
            value is LocalDate -> Constant(value)
            value is kotlin.Array<*> -> {
                @Suppress("UNCHECKED_CAST")
                Array(value.map { serialize(it) }.toTypedArray()) as Expression<E, T>
            }
            value is DataRow -> Row(value.entries.associate { (key, value) -> key to serialize(value) })
            value::class.isData -> {
                val fields = value::class.ensuredPrimaryConstructor().parameters
                    .map { param ->
                        @Suppress("UNCHECKED_CAST")
                        (value::class as KClass<T>).memberProperties.single { it.name == param.name }
                    }
                Row(fields.associate { it.name to serialize(it.get(value)) })
            }
            else -> throw IllegalStateException("Unsupported value type ${value::class.qualifiedName}.")
        }

    fun <T : Any> deserializeQueryResult(queryResult : R, type: KClass<T>): List<T>
}

fun <T : Any> KClass<T>.ensuredPrimaryConstructor() = primaryConstructor ?: throw IllegalStateException("$qualifiedName is lacking a primary constructor.")
