package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * SQL `COALESCE(...)` function that returns the first non-`NULL` value from its arguments or `NULL` if all are `NULL`.
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of all arguments
 */
class Coalesce<E : Engine<E>, T>(
    vararg expressions: Expression<E, T>,
) : Expression<E, T> {
    companion object {
        /**
         * Creates a [Coalesce] expression that is known to be non-nullable, because its last argument is non-nullable.
         *
         * @param nullableExpressions the expressions to coalesce except the last one
         * @param nonNullableExpression the last expression that is non-nullable
         * @return a [Coalesce] expression with the given arguments
         */
        operator fun <E : Engine<E>, T : Any> invoke(vararg nullableExpressions: Expression<E, T?>, nonNullableExpression: Expression<E, T>): Coalesce<E, T> {
            @Suppress("UNCHECKED_CAST") // knowing the last expression is non-nullable, we can expect the entire Coalesce to be non-nullable
            return Coalesce(*nullableExpressions, nonNullableExpression) as Coalesce<E, T>
        }
    }

    val expressions: List<Expression<E, T>> = expressions.toList()
    override val subexpressions = expressions.toList()

    override fun sql() = "COALESCE(${this@Coalesce.expressions.joinToString(",") { it.sql() }})"

    override fun defaultColumnName() = "COALESCE(${this@Coalesce.expressions.joinToString(",") { it.defaultColumnName() }})"

    override fun equals(other: Any?) = other is Coalesce<E, T>
            && this@Coalesce.expressions == other.expressions

    override fun hashCode() = expressions.hashCode()
}
