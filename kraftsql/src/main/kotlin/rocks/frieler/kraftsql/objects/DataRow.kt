package rocks.frieler.kraftsql.objects

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class DataRow(
    val values: Map<String, Any?>
) {
    operator fun get(field: String): Any? {
        check(field in values.keys) { "no field '$field' in DataRow; did you mean one of ${values.keys}?" }
        return values[field]
    }

    operator fun plus(other: DataRow) = DataRow(this.values + other.values)

    override fun toString(): String {
        return "DataRow(${values.entries.joinToString(", ") { "${it.key}=${it.value}" }})"
    }

    override fun equals(other: Any?) = other is DataRow && values == other.values

    override fun hashCode() = values.hashCode()

    companion object {
        fun from(obj: Any) =
            obj as? DataRow ?: DataRow(
                (obj::class as KClass<Any>)
                    .memberProperties
                    .associate { field -> field.name to field.get(obj) })
    }
}
