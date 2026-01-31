package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2Engine

class ArrayConcatenation<T>(
    private val left: Expression<H2Engine, Array<T>?>,
    private val right: Expression<H2Engine, Array<T>?>,
) : rocks.frieler.kraftsql.expressions.ArrayConcatenation<H2Engine, T>(arrayOf(left, right)) {
    override val subexpressions = listOf(left, right)

    override fun sql() = "(${left.sql()}) || (${right.sql()})"

    override fun defaultColumnName() = "${left.defaultColumnName()} || ${right.defaultColumnName()}"
}

@Suppress("DANGEROUS_CHARACTERS")
infix fun <T> Expression<H2Engine, Array<T>?>.`||`(other: Expression<H2Engine, Array<T>?>) =
    ArrayConcatenation(this, other)
