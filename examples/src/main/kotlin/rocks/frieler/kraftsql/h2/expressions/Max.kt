package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Aggregation
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2Engine

class Max(
    val expression: Expression<H2Engine, Int>,
) : Aggregation<H2Engine, Int> {
    override val subexpressions = listOf(expression)

    override fun sql() = "MAX(${expression.sql()})"

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
