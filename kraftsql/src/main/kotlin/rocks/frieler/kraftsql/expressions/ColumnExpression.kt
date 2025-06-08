package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class ColumnExpression<E : Engine<E>, T>(
    val value: Expression<E, T>,
    val alias: String? = null,
) : Expression<E, T> { // FIXME: Cannot serve as an Expression in all places, just intended for SELECTs

    override fun sql() = value.sql() + if (alias != null) " AS \"$alias\"" else ""
}
