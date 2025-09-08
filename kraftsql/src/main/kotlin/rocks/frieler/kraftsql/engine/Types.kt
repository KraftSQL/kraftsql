package rocks.frieler.kraftsql.engine

interface Types<E : Engine<E>> {
    fun parseType(type: String) : Type<E, *>
}
