package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Array<E : Engine<E>, T : Any>(
    val elements: kotlin.Array<Expression<E, T>>?,
) : Expression<E, kotlin.Array<T?>> {
    override fun sql(): String {
        if (elements == null) {
            return "NULL"
        }
        return "ARRAY [${elements.joinToString(",") { it.sql() }}]"
    }

    override fun defaultColumnName(): String {
        if (elements == null) {
            return "NULL"
        }
        return "[${elements.joinToString(",") { it.defaultColumnName() }}]"
    }

    override fun equals(other: Any?) = other is Array<E, *> && elements.contentEquals(other.elements)

    override fun hashCode() = elements.hashCode()

    companion object {
        operator fun <E : Engine<E>, T : Any> invoke(vararg elements: Expression<E, T>) = Array(elements.toList().toTypedArray())
    }
}
