package rocks.frieler.kraftsql.engine

import kotlin.reflect.KType

interface Engine<E : Engine<E>> {
    fun getTypeFor(type: KType): Type
}
