package rocks.frieler.kraftsql.data

import java.util.Objects

/**
 * A row of data, i.e., a sequence of values associated with the field names of a schema.
 */
class DataRow(
    val entries: Iterable<Pair<String, Any?>>
) {
    constructor(vararg entries: Pair<String, Any?>) : this(entries.toList())

    val fieldNames : List<String> = entries.map { it.first }.apply {
        require(toSet().size == size) { "Field names must be unique." }
    }

    private val values = entries.associateBy({ it.first }, { it.second })

    operator fun get(field: String): Any? {
        check(field in fieldNames) { "No field '$field' in DataRow; did you mean one of $fieldNames?" }
        return values[field]
    }

    operator fun plus(other: DataRow) = DataRow(this.entries + other.entries)

    override fun toString(): String {
        return "DataRow(${entries.joinToString(", ") { (field, value) ->
            "${field}=${
                when (value) {
                    is Array<*> -> value.contentDeepToString()
                    else -> value
                }
            }"
        }})"
    }

    override fun equals(other: Any?) =
        other is DataRow
            && fieldNames == other.fieldNames
            && entries.all { (key, value) -> Objects.deepEquals(value, other[key]) }

    override fun hashCode() = entries.hashCode()
}
