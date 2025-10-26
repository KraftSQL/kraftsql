package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * `IS NOT NULL`-[Expression] that checks, if the given expression is not `NULL`.
 *
 * @param <E> the SQL [Engine]
 * @param expression the expression to check
 */
class IsNotNull<E : Engine<E>>(
    val expression: Expression<E, *>,
) : Expression<E, Boolean> {
    override fun sql() = "${expression.sql()} IS NOT NULL"

    override fun defaultColumnName() = "${expression.defaultColumnName()}_IS_NOT_NULL"

    override fun equals(other: Any?) = other is IsNotNull<*> && expression == other.expression

    override fun hashCode() = Objects.hash(expression)
}
