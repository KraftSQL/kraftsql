package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Sum<E : Engine<E>, T : Number>(
    val column: Expression<E, T>,
) : Aggregation<E, T> { // FIXME: SUM() actually aggregates to either Long or Double.
    override fun sql() = "SUM(${column.sql()})"

    override fun defaultColumnName() = "SUM(${column.defaultColumnName()})"

    override fun equals(other: Any?) = other is Sum<E, T> && column == other.column

    override fun hashCode() = column.hashCode()
}
