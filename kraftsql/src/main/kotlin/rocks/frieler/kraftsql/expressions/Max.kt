package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Max<E : Engine<E>, T : Comparable<T>?>(
    val expression: Expression<E, T?>,
) : Aggregation<E, T> {
    override fun sql() = "MAX(${expression.sql()})"

    override fun defaultColumnName(): String {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?) = other is Max<E, T>
            && expression == other.expression

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }
}
