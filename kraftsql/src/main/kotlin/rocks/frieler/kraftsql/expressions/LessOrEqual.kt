package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * SQL '<=' operator.
 *
 * @param E the [Engine] targeted
 * @param left the left-hand side of the '<='-expression
 * @param right the right-hand side of the '<='-expression
 */
class LessOrEqual<E : Engine<E>>(
    val left: Expression<E, *>,
    val right: Expression<E, *>,
) : Expression<E, Boolean?> {
    override fun sql() = "(${left.sql()})<=(${right.sql()})"

    override fun defaultColumnName() = "${left.defaultColumnName()}<=${right.defaultColumnName()}"

    override fun equals(other: Any?) = other is LessOrEqual<E>
            && left == other.left
            && right == other.right

    override fun hashCode() = Objects.hash(left, right)
}

/**
 * Short infix syntax for [rocks.frieler.kraftsql.expressions.LessOrEqual].
 *
 * @param E the [Engine] targeted
 */
infix fun <E : Engine<E>> Expression<E, *>.lessOrEqual(other: Expression<E, *>): LessOrEqual<E> = LessOrEqual(this, other)
