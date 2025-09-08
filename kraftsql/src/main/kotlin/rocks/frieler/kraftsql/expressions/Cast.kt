package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.Type

class Cast<E : Engine<E>, T : Any>(
    val expression: Expression<E, *>,
    val type: Type<E, T>,
) : Expression<E, T> {
    override fun sql() = "CAST(${expression.sql()} AS ${type.sql()})"

    override fun defaultColumnName() = "CAST(${expression.defaultColumnName()} AS ${type.sql()})"

    override fun equals(other: Any?) = other is Cast<*, *> && expression == other.expression && type == other.type

    override fun hashCode() = expression.hashCode() + type.hashCode()
}
