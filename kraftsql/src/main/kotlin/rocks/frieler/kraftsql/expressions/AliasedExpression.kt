package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.queries.Selectable

class AliasedExpression<E : Engine<E>, T>(
    val value: Expression<E, T>,
    val alias: String? = null,
) : Selectable<E> {
    override fun sql() = value.sql() + if (alias != null) " AS \"$alias\"" else ""
}
