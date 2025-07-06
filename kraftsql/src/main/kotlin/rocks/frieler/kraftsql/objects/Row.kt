package rocks.frieler.kraftsql.objects

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class Row(
    val values: Map<String, Any?>
) {
    operator fun get(field: String): Any? {
        check(field in values.keys) { "no field '$field' in Row; did you mean one of ${values.keys}?" }
        return values[field]
    }

    operator fun plus(other: Row) = Row(this.values + other.values)

    override fun toString(): String {
        return "Row(${values.entries.joinToString(", ") { "${it.key}=${it.value}" }})"
    }

    companion object {
        fun from(obj: Any) =
            obj as? Row ?: Row(
                (obj::class as KClass<Any>)
                    .memberProperties
                    .associate { field -> field.name to field.get(obj) })
    }
}
