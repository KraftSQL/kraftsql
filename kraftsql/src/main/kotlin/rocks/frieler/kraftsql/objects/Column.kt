package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.Type

class Column<E : Engine<E>>(
    val name: String,
    val type: Type,
) {
    fun sql() : String {
        return "\"$name\" ${type.sql()}"
    }
}
