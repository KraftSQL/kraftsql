package rocks.frieler.kraftsql.engine

import kotlin.reflect.KType

interface Type<E : Engine<E>, T : Any> {
    fun sql(): String

    fun naturalKType() : KType
}
