package rocks.frieler.kraftsql.objects

class Row(
    val values: Map<String, Any?>
) {
    operator fun get(field: String): Any? {
        check(field in values.keys) { "no field '$field' in Row; did you mean one of ${values.keys}?" }
        return values[field]
    }

    override fun toString(): String {
        return "Row(${values.entries.joinToString(", ") { "${it.key}=${it.value}" }})"
    }
}
