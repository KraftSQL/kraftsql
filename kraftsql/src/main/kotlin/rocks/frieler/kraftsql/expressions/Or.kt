package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects

class Or<E: Engine<E>>(val left: Expression<E, Boolean?>, val right: Expression<E, Boolean?>): Expression<E, Boolean> {
    override fun sql(): String = "${left.sql()} OR ${right.sql()}"

    override fun defaultColumnName(): String = "${left.defaultColumnName()}_OR_${right.defaultColumnName()}"

    override fun equals(other: Any?): Boolean = other is Or<*>
            && left == other.left
            && right == other.right

    override fun hashCode(): Int = Objects.hash(left, right)
}