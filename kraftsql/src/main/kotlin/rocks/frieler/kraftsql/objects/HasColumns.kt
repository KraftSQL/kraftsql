package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import kotlin.reflect.KProperty1

interface HasColumns<E : Engine<E>, T> {

    operator fun get(field: String) = Column<E, Any?>(field)

    operator fun <V> get(property: KProperty1<T, V>) : Column<E, V> {
        @Suppress("UNCHECKED_CAST")
        return this[property.name] as Column<E, V>
    }
}
