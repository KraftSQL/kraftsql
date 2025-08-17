package rocks.frieler.kraftsql.objects

import java.util.Arrays
import java.util.Objects
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class DataRow(
    val values: Map<String, Any?>
) {
    operator fun get(field: String): Any? {
        var fieldName = field
        while (fieldName !in values && fieldName.isNotEmpty()) {
            fieldName = fieldName.substringBeforeLast(".", missingDelimiterValue = "")
        }
        if (fieldName == field) {
            return values[fieldName]
        } else if (fieldName.isEmpty()) {
            throw IllegalStateException("No field '$field' in DataRow; did you mean one of ${values.keys}?")
        }

        val subfieldName = field.removePrefix("${fieldName}.")
        if (values[fieldName] is DataRow) {
            return (values[fieldName] as DataRow)[subfieldName]
        }

        throw IllegalStateException("Field '$field' is not a DataRow with subfield '$subfieldName'.")
    }

    operator fun plus(other: DataRow) = DataRow(this.values + other.values)

    override fun toString(): String {
        return "DataRow(${values.entries.joinToString(", ") { "${it.key}=${it.value}" }})"
    }

    override fun equals(other: Any?) =
        other is DataRow
                && values.size == other.values.size
                && values.all { (key, value) -> Objects.deepEquals(value, other.values[key]) }

    override fun hashCode() = values.hashCode()

    companion object {
        fun from(obj: Any) =
            obj as? DataRow ?: DataRow(
                (obj::class as KClass<Any>)
                    .memberProperties
                    .associate { field -> field.name to field.get(obj) })
    }
}
