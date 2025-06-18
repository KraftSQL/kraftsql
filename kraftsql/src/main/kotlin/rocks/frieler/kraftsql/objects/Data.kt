package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine

interface Data<E : Engine<E>, T : Any> : HasColumns<E, T> {
    fun sql(): String
}
