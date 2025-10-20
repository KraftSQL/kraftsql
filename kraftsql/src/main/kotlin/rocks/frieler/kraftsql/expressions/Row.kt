package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

/**
 * The SQL `Row(...)` [Expression] that creates a row from the given named sub-[Expression].
 *
 * @param <E> the [Engine] that implements [Row] and for which the SQL code is rendered
 * @param <T> the Kotlin type of the row's value
 * @param values the named sub-[Expression]s to create the row from
 */
open class Row<E : Engine<E>, T : Any>(
    val values: Map<String, Expression<E, *>>?
) : Expression<E, T> {
    override fun sql(): String {
        if (values == null) {
            return "NULL"
        }
        return "(${values.entries.joinToString(", ") { (key, value) -> "${value.sql()} AS `$key`" }})"
    }

    override fun defaultColumnName(): String {
        if (values == null) {
            return "NULL"
        }
        return values.entries.joinToString(",") { (key, value) -> "$key:${value.defaultColumnName()}" }
    }

    override fun equals(other: Any?) = other is Row<E, *> && values == other.values

    override fun hashCode() = values.hashCode()
}
