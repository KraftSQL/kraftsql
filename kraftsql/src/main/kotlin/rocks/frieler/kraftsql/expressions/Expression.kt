package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.data.Type

/**
 * An expression, that evaluates to a value for each row of [Data][rocks.frieler.kraftsql.data.Data].
 *
 * @param T the Kotlin type of the [Expression]'s value
 */
interface Expression<out T> {
    /**
     * The result type of this [Expression]. Must comply with [T].
     */
    val type: Type

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}
