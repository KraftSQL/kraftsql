package rocks.frieler.kraftsql.objects

import java.util.Objects

/**
 * A row of data, i.e., a sequence of values associated with the column names of a schema.
 */
class DataRow(
    val entries: Iterable<Pair<String, Any?>>
) {
    constructor(vararg entries: Pair<String, Any?>) : this(entries.toList())

    val columnNames : List<String> = entries.map { it.first }.apply {
        require(toSet().size == size) { "Column names must be unique." }
    }

    private val values = entries.associateBy({ it.first }, { it.second })

    operator fun get(field: String): Any? {
        var fieldName = field
        while (fieldName !in columnNames && fieldName.isNotEmpty()) {
            fieldName = fieldName.substringBeforeLast(".", missingDelimiterValue = "")
        }
        if (fieldName == field) {
            return values[fieldName]
        } else if (fieldName.isEmpty()) {
            throw IllegalStateException("No field '$field' in DataRow; did you mean one of $columnNames?")
        }

        val subfieldName = field.removePrefix("${fieldName}.")
        val rowField = values[fieldName] as? DataRow
            ?: throw IllegalStateException("Field '$field' is not a DataRow with subfield '$subfieldName'.")
        return (rowField)[subfieldName]
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
                && columnNames == other.columnNames
                && entries.all { (key, value) -> Objects.deepEquals(value, other[key]) }

    override fun hashCode() = entries.hashCode()
}
