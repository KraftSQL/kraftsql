package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * SQL 'AND' operator.

 * @param E the [Engine] that implements this [And] and for which the SQL code is rendered
 * @param left the left-hand side of the 'AND'-expression
 * @param right the right-hand side of the 'AND'-expression
 */
class And<E : Engine<E>>(
    val left: Expression<E, Boolean?>,
    val right: Expression<E, Boolean?>,
) : Expression<E, Boolean> {
    override val subexpressions = listOf(left, right)

    override fun sql() = "(${left.sql()}) AND (${right.sql()})"

    override fun defaultColumnName() = "${left.defaultColumnName()}_AND_${right.defaultColumnName()}"

    override fun equals(other: Any?) = other is And<*>
            && left == other.left
            && right == other.right

    override fun hashCode() = Objects.hash(left, right)
}
