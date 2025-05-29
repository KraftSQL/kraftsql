package rocks.frieler.kraftsql.models

class Row(
    val values: Map<String, Any?>
) {
    operator fun get(field: String): Any? {
        check(field in values.keys) { "no field '$field' in Row; did you mean one of ${values.keys}?" }
        return values[field]
    }
}
