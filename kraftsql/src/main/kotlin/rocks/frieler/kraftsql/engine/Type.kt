package rocks.frieler.kraftsql.engine

interface Type<E : Engine<E>> {
    fun sql(): String
}
