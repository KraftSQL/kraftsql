package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Sum<E : Engine<E>, T : Number>(
    private val column: Expression<E, T>,
) : Aggregation<E, T> {
    override fun sql() = "SUM(${column.sql()})"
}
