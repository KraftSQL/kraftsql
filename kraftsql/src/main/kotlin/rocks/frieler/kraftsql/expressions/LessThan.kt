package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class LessThan<E : Engine<E>>(
    val left: Expression<E, *>,
    val right: Expression<E, *>,
) : Expression<E, Boolean?> {
    override fun sql() = "(${left.sql()})<(${right.sql()})"

    override fun defaultColumnName(): String {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }
}
