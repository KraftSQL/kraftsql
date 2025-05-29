package rocks.frieler.kraftsql.ddl

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.Type

class ColumnDefinition<E : Engine<E>>(
    private val name: String,
    private val type: Type,
) {
    fun sql() : String {
        return "`$name` ${type.sql()}"
    }
}
