package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.And
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.expressions.Sum

/**
 * Interface for simulator utility to collect subexpressions of [Expression]s.
 *
 * @param E the [Engine] to simulate
 */
interface SubexpressionCollector<E : Engine<E>> {
    /**
     * Retrieves the direct subexpressions of the given [Expression].
     *
     * @param expression the [Expression] for which the subexpressions are to be collected
     * @return a list of direct subexpressions of the provided [Expression]
     */
    fun getSubexpressions(expression: Expression<E, *>) : List<Expression<E, *>>

    /**
     * Collects all subexpressions of the given [Expression] recursively.
     *
     * @param expression the [Expression] for which the subexpressions are to be collected
     * @return a list of all subexpressions below the provided [Expression]
     */
    fun collectAllSubexpressions(expression: Expression<E, *>): List<Expression<E, *>> = listOf(expression) + getSubexpressions(expression).flatMap { collectAllSubexpressions(it) }
}

/**
 * Generic [SubexpressionCollector] that collects subexpressions for common SQL [Expression]s.
 *
 * @param E the [Engine] associated with the SQL expressions being processed
 */
open class GenericSubexpressionCollector<E : Engine<E>> : SubexpressionCollector<E> {
    override fun getSubexpressions(expression: Expression<E, *>) =
        when (expression) {
            is And<E> -> listOf(expression.left, expression.right)
            is Array<E, *> -> (expression.elements ?: emptyArray()).toList()
            is ArrayConcatenation<E, *> -> expression.arguments.toList()
            is Cast<E, *> -> listOf(expression.expression)
            is Coalesce<E, *> -> expression.expressions
            is Column<E, *> -> emptyList()
            is Constant<E, *> -> emptyList()
            is Count<E> -> listOfNotNull(expression.expression)
            is Equals<E> -> listOf(expression.left, expression.right)
            is IsNotNull<E> -> listOf(expression.expression)
            is Row<E, *> -> (expression.values ?: emptyMap()).values.toList()
            is Sum<E, *> -> listOf(expression.expression)
            else -> throw NotImplementedError("Subexpressions not implemented for ${expression::class.qualifiedName}")
        }
}
