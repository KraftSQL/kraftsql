package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * `IS NULL`-[Expression] that checks, if the given expression is `NULL`.
 *
 * @param E the SQL [Engine]
 * @param expression the expression to check
 */
class IsNull<E : Engine<E>>(
    val expression: Expression<E, *>,
) : Expression<E, Boolean> {
    override fun sql() = "${expression.sql()} IS NULL"

    override fun equals(other: Any?) = other is IsNull<*> && expression == other.expression

    override fun hashCode() = Objects.hash(expression)
}
