package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table

/**
 * [EngineState] that overlays another [EngineState] to gather changes made during a transaction until they're committed
 * or rolled back.
 *
 * @param E the [Engine]
 * @param parent the [EngineState] to overlay
 */
open class TransactionStateOverlay<E : Engine<E>>(
    val parent: EngineState<E>,
) : EngineState<E>() {
    override fun containsTable(name: String): Boolean {
        return super.containsTable(name) || parent.containsTable(name)
    }

    override fun findTable(name: String): Pair<Table<E, *>, MutableList<DataRow>>? {
        return super.findTable(name) ?: parent.findTable(name)
    }

    fun ensureTableCopy(name: String) {
        tables.computeIfAbsent(name) { getTable(name).run { first to second.toMutableList() } }
    }

    fun commitIntoParent(): EngineState<E> {
        tables.values.forEach { (table, data) -> parent.writeTable(table, data) }
        return parent
    }
}
