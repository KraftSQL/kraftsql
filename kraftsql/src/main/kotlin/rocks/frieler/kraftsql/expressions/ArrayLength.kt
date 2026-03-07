package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import kotlin.Array

/**
 * The `ARRAY_LENGTH()` function, that returns the length of an array [Expression].
 *
 * @param E the SQL [Engine]
 * @param array the array [Expression] to get the length of
 */
class ArrayLength<E : Engine<E>>(
    val array: Expression<E, Array<*>?>,
) : Expression<E, Int?> {
    override fun sql() = "ARRAY_LENGTH(${array.sql()})"

    override fun defaultColumnName() = "ARRAY_LENGTH(${array.defaultColumnName()})"

    override fun equals(other: Any?) = other is ArrayLength<E>
            && array == other.array

    override fun hashCode() = array.hashCode()
}
