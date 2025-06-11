package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.queries.Selectable

interface Expression<E: Engine<E>, T> : Selectable<E> {
    override fun sql(): String
}
