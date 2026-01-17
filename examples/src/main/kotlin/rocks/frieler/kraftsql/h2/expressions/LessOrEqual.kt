package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2Engine

class LessOrEqual(
    val left: Expression<H2Engine, *>,
    val right: Expression<H2Engine, *>,
) : Expression<H2Engine, Boolean> {
    override val subexpressions = listOf(left, right)

    override fun sql() = "(${left.sql()})<=(${right.sql()})"

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
