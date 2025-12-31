package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine

// TODO: Move into external class, not an interface implemented by Data instances and wrappers
interface HasSchema<E : Engine<E>> {
    fun inferSchema(): List<Column<E>>
}
