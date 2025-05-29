package rocks.frieler.kraftsql.ddl

import rocks.frieler.kraftsql.engine.Engine

class ColumnDefinition<E : Engine<E>>(
    private val name: String,
    private val type: String,
) {
    fun sql() : String {
        return "`$name` $type"
    }
}
