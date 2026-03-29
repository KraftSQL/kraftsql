package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * SQL 'OR' operator.

 * @param E the [Engine] that implements this [Or] and for which the SQL code is rendered
 * @param left the left-hand side of the 'OR'-expression
 * @param right the right-hand side of the 'OR'-expression
 */
class Or<E : Engine<E>>(
    val left: Expression<E, Boolean?>,
    val right: Expression<E, Boolean?>,
) : Expression<E, Boolean?> {
    override fun sql() = "(${left.sql()}) OR (${right.sql()})"

    override fun defaultColumnName() = "${left.defaultColumnName()}_OR_${right.defaultColumnName()}"

    override fun equals(other: Any?) = other is Or<*>
            && left == other.left
            && right == other.right

    override fun hashCode() = Objects.hash(left, right)
}
