package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import kotlin.reflect.KProperty1

interface HasColumns<E : Engine<E>, T : Any> {

    operator fun <V : Any> get(field: String) = Column<E, V>(field)

    operator fun <V : Any> get(property: KProperty1<T, V>) : Column<E, V> = this[property.name]
}
