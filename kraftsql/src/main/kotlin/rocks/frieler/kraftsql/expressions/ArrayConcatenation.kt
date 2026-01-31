package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects
import kotlin.Array

/**
 * Partial implementation for an [Expression] that concatenates [rocks.frieler.kraftsql.expressions.Array]s.
 *
 * Note: Such an expression is not part of standard SQL, but several SQL engines offer it with varying implementations
 * and behaviors. Hence, the **Kraft**SQL core provides this partial support for connectors to build on by implementing
 * this class.
 *
 * @param E the [Engine] that implements [ArrayConcatenation] and for which the SQL code is rendered
 * @param T the Kotlin type of the arrays' elements
 * @param arguments the Array-typed [Expression]s to concatenate
 */
abstract class ArrayConcatenation<E : Engine<E>, T>(
    val arguments: Array<Expression<E, Array<T>?>>,
) : Expression<E, Array<T>?> {

    override fun equals(other: Any?) = other is ArrayConcatenation<E, T>
            && arguments.contentDeepEquals(other.arguments)

    override fun hashCode() = Objects.hash(*arguments)
}
