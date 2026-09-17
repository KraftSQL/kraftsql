package rocks.frieler.kraftsql.data

import java.util.Objects

/**
 * Definition of a column of structured, tabular data.
 *
 * @param name the name of the column
 * @param type the [Type] of the column's values
 */
class Column(
    val name: String,
    val type: Type,
) {
    override fun toString() = "\"$name\": $type"

    override fun equals(other: Any?) = other is Column
            && name == other.name
            && type == other.type

    override fun hashCode() = Objects.hash(name, type)
}
