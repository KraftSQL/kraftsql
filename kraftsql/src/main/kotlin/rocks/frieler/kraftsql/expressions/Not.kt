package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * SQL 'NOT' operator.
 *
 * @param E the [Engine] that implements this [Not] and for which the SQL code is rendered
 * @param expression the expression to negate
 */
class Not<E : Engine<E>>(
    val expression: Expression<E, Boolean?>,
) : Expression<E, Boolean?> {
    override fun sql() = "NOT (${expression.sql()})"

    override fun defaultColumnName() = "NOT_${expression.defaultColumnName()}"

    override fun equals(other: Any?) = other is Not<*>
            && expression == other.expression

    override fun hashCode() = Objects.hash(expression)
}
