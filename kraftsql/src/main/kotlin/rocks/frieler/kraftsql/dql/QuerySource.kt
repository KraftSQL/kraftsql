package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.HasColumns

/**
 * A wrapper around [Data] to use it in a query.
 *
 * @param E the [Engine] targeted
 * @param T the Kotlin type of the [Data]'s rows
 * @param data the [Data] to use in the query
 * @param alias an optional alias for the [Data]
 */
open class QuerySource<E: Engine<E>, T : Any>(
    val data: Data<E, T>,
    val alias: String? = null,
) : HasColumns<E, T> {

    /**
     * Renders the SQL portion to embed this [QuerySource] in a query.
     *
     * @return the SQL portion to embed this [QuerySource] in a query
     */
    fun sql() = data.sql()
        .let { sql -> if (data is Command<*, *>) "($sql)" else sql }
        .let { sql -> if (alias != null) "$sql AS `$alias`" else sql }

    /**
     * The names of the available [rocks.frieler.kraftsql.objects.Column]s, which are the columns of the underlying
     * [Data], prefixed with this [QuerySource]'s alias if set.
     */
    override val columnNames: List<String>
        get() = data.columnNames.map { if (alias == null) it else "$alias.${it}" }

    /**
     * Retrieves a [Column] expression for the named column.
     *
     * The name must be available in the underlying [Data].
     *
     * If set, this [QuerySource]'s alias is added as qualifier to the [Column] expression, but must not be part of the
     * given column name.
     *
     * @param column the name of the column
     * @return a [Column] expression for the named column
     */
    override operator fun get(column: String) : Column<E, Any?> =
        if (alias != null && column.startsWith("$alias.") && column.removePrefix("$alias.") in data.columnNames) {
            get(column.removePrefix("$alias."))
        } else {
            data[column].let { if (alias != null) it.withQualifier(alias) else it }
        }
}
