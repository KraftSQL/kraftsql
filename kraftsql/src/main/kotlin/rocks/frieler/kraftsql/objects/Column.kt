package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.Type
import java.util.Objects

/**
 * Definition of a column of relational data such as a [Table].
 *
 * @param <E> the SQL [Engine]
 * @param name the name of the column
 * @param type the [Type] of the column's values
 * @param nullable whether the column allows `NULL` values
 */
class Column<E : Engine<E>>(
    val name: String,
    val type: Type<E, *>,
    val nullable: Boolean = true,
) {
    fun sql() : String {
        return "$name ${type.sql()}${ if (!nullable) " NOT NULL" else ""}"
    }

    override fun equals(other: Any?) = other is Column<*>
            && name == other.name
            && type == other.type
            && nullable == other.nullable

    override fun hashCode() = Objects.hash(name, type, nullable)
}
