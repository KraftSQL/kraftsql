package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * SQL '>=' operator.
 *
 * @param E the [Engine] targeted
 * @param left the left-hand side of the '>='-expression
 * @param right the right-hand side of the '>='-expression
 */
class GreaterOrEqual<E : Engine<E>>(
    val left: Expression<E, *>,
    val right: Expression<E, *>,
) : Expression<E, Boolean?> {
    override fun sql() = "(${left.sql()})>=(${right.sql()})"

    override fun equals(other: Any?) = other is GreaterOrEqual<E>
            && left == other.left
            && right == other.right

    override fun hashCode() = Objects.hash(left, right)
}

/**
 * Short infix syntax for [rocks.frieler.kraftsql.expressions.GreaterOrEqual].
 *
 * @param E the [Engine] targeted
 */
infix fun <E : Engine<E>> Expression<E, *>.greaterOrEqual(other: Expression<E, *>): GreaterOrEqual<E> = GreaterOrEqual(this, other)