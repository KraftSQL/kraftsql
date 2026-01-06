package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.Type

/**
 * SQL CAST() function that casts a value to a certain type.
 *
 * @param <E> the [Engine] targeted
 * @param <T> the Kotlin type of the [Cast]'s target type
 * @param expression the [Expression] to cast
 * @param type the target type to cast to
 */
class Cast<E : Engine<E>, T> : Expression<E, T> {
    val expression: Expression<E, *>
    val type: Type<E, T & Any>

    private constructor(expression: Expression<E, *>, type: Type<E, T & Any>) {
        this.expression = expression
        this.type = type
    }

    override fun sql() = "CAST(${expression.sql()} AS ${type.sql()})"

    override fun defaultColumnName() = "CAST(${expression.defaultColumnName()} AS ${type.sql()})"

    override fun equals(other: Any?) = other is Cast<*, *> && expression == other.expression && type == other.type

    override fun hashCode() = expression.hashCode() + type.hashCode()

    companion object {
        @JvmName("CastNullable")
        operator fun <E : Engine<E>, T : Any> invoke(expression: Expression<E, Any?>, type: Type<E, T>) : Cast<E, T?> =
            Cast(expression, type)

        operator fun <E : Engine<E>, T : Any> invoke(expression: Expression<E, Any>, type: Type<E, T>) : Cast<E, T> =
            Cast(expression, type)
    }
}
