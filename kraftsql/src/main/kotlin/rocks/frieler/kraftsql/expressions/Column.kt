package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.Type
import java.util.Objects

/**
 * An [Expression] that references a column of [Data] by its name.
 *
 * @param T the Kotlin type of the column's values
 * @param data the [Data] the column belongs to
 * @param name the name of the column in the [Data]'s [schema][Data.schema]
 * @throws IllegalArgumentException if there is no column with the given name in the [Data]'s [schema][Data.schema]
 */
class Column<T>(
    val data: Data<*>,
    val name: String,
) : Expression<T> {
    init {
        require(data.schema.any { it.name == name }) { "No column '$name' in Data; did you mean one of ${data.schema.map { it.name }}?" }
    }

    override val type: Type = data.schema.single { it.name == name }.type

    override fun toString() = name

    override fun equals(other: Any?) = other is Column<*>
            && data === other.data // by identity, so that distinct but equal Data are still different sources
            && name == other.name

    override fun hashCode() = Objects.hash(System.identityHashCode(data), name)
}
