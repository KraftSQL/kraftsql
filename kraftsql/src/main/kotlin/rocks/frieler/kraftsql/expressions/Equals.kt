package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * The SQL '=' operator.
 *
 * @param E the [Engine] that implements this [Equals] and for which the SQL code is rendered
 * @param left the left-hand side of the '='-expression
 * @param right the right-hand side of the '='-expression
 */
class Equals<E : Engine<E>>(
    val left: Expression<E, *>,
    val right: Expression<E, *>,
) : Expression<E, Boolean> {
    override fun sql() = "(${left.sql()})=(${right.sql()})"

    override fun defaultColumnName() = "${left.defaultColumnName()} = ${right.defaultColumnName()}"

    override fun equals(other: Any?) = other is Equals<E> && left == other.left && right == other.right

    override fun hashCode() = Objects.hash(left, right)
}

infix fun <E : Engine<E>> Expression<E, *>.`=`(other: Expression<E, *>): Equals<E> = Equals(this, other)
