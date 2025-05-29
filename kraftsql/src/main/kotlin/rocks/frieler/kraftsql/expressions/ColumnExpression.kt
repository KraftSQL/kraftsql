package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class ColumnExpression<E : Engine<E>, T>(
    val value: Expression<E, T>,
    val alias: String? = null,
) : Expression<E, T> {

    override fun sql() = value.sql() + if (alias != null) " AS \"$alias\"" else ""
}
