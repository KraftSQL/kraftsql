package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import java.util.Objects
import kotlin.Array

/**
 * Reference to an element of an array [Expression].
 *
 * Note that most arrays are 1-based in most, but not every SQL engine.
 *
 * @param E the SQL [Engine]
 * @param T the type of the array's elements
 * @param array the array [Expression] to reference
 * @param index the index of the element to reference
 */
class ArrayElementReference<E : Engine<E>, T>(
    val array: Expression<E, Array<T>?>,
    val index: Expression<E, Int>,
) : Expression<E, T?> {
    override fun sql() = "${array.sql()}[${index.sql()}]"

    override fun defaultColumnName() = "${array.defaultColumnName()}[${index.defaultColumnName()}]"

    override fun equals(other: Any?) = other is ArrayElementReference<E, T>
            && array == other.array
            && index == other.index

    override fun hashCode() = Objects.hash(array, index)

    companion object {
        operator fun <E : Engine<E>, T> Expression<E, Array<T>?>.get(index: Expression<E, Int>) = ArrayElementReference(this, index).knownNotNull()
    }
}
