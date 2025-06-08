package rocks.frieler.kraftsql.models

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import kotlin.reflect.KProperty1

interface HasColumns<E : Engine<E>, T> {
    operator fun <V> get(field: String) = Column<E, V>(field)

    operator fun <V> get(property: KProperty1<T, V>) : Column<E, V> = this[property.name]
}
