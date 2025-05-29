package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Count<E : Engine<E>> : Expression<E, Long> {
    override fun sql() = "COUNT(*)"
}
