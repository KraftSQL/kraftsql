package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

/**
 * The SQL `!=` operator.
 *
 * @param E the [Engine] that implements this [NotEquals] and for which the SQL code is rendered
 * @param left the left-hand side of the '!='-expression
 * @param right the right-hand side of the '!='-expression
 */
class NotEquals<E : Engine<E>>(
    val left: Expression<E, *>,
    val right: Expression<E, *>,
) : Expression<E, Boolean> {
    override fun sql() = "(${left.sql()})!=(${right.sql()})"

    override fun equals(other: Any?) = other is NotEquals<E> && left == other.left && right == other.right

    override fun hashCode() = Objects.hash(left, right)
}

/**
 * Convenience function to create a [NotEquals] expression using infix notation.
 *
 * @param E the [Engine] that implements this [NotEquals] and for which the SQL code is rendered
 * @param other the right-hand side of the '!='-expression
 */
@Suppress("FunctionName")
infix fun <E : Engine<E>> Expression<E, *>.`!=`(other: Expression<E, *>) = NotEquals(this, other)
