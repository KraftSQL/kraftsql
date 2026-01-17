package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2Engine

class ArrayLength(
    val array: Expression<H2Engine, Array<*>>,
) : Expression<H2Engine, Int> {
    override val subexpressions = listOf(array)

    override fun sql() = "ARRAY_LENGTH(${array.sql()})"

    override fun defaultColumnName(): String {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }
}
