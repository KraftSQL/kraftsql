package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table

open class EngineState<E : Engine<E>> {
    protected val tables: MutableMap<String, Pair<Table<E, *>, MutableList<DataRow>>> = mutableMapOf()

    open fun containsTable(name: String) = name in tables

    open fun findTable(name: String) = tables[name]

    open fun getTable(name: String) = findTable(name) ?: throw IllegalStateException("Table '$name' does not exist.")

    open fun addTable(table: Table<E, *>) {
        tables[table.qualifiedName] = Pair(table, mutableListOf())
    }

    open fun removeTable(table: Table<E, *>) {
        tables.remove(table.qualifiedName)
    }

    open fun writeTable(table: Table<E, *>, data: List<DataRow>) {
        tables[table.qualifiedName] = Pair(table, data.toMutableList())
    }
}
