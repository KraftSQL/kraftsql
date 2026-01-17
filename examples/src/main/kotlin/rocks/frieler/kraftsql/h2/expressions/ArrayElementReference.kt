package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2Engine

class ArrayElementReference<T>(
    val array: Expression<H2Engine, Array<T>>,
    val index: Expression<H2Engine, Int>,
) : Expression<H2Engine, T> {
    override val subexpressions = listOf(array, index)

    override fun sql() = "${array.sql()}[${index.sql()}]"

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
