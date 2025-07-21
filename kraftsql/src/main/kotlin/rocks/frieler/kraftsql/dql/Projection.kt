package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression

class Projection<E : Engine<E>, T>(
    val value: Expression<E, T>,
    val alias: String? = null,
) {
    fun sql() = value.sql() + if (alias != null) " AS `$alias`" else ""
}
