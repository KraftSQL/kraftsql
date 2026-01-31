package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Data
import java.util.Objects

/**
 * A SQL `SELECT` statement.
 *
 * @param E the [Engine] where this [Select] can be executed
 * @param T the Kotlin type of the rows this [Select] returns
 */
open class Select<E : Engine<E>, T : Any>(
    val source: QuerySource<E, *>,
    val joins: List<Join<E>> = emptyList(),
    val columns: List<Projection<E, *>>? = null,
    val filter: Expression<E, Boolean?>? = null,
    val grouping: List<Expression<E, *>> = emptyList(),
) : Command<E, List<T>>, Data<E, T> {

    /**
     * The names of the columns this [Select] returns.
     */
    @Suppress("IfThenToElvis")
    override val columnNames: List<String>
        get() =
            if (columns != null) {
                columns.map { it.alias ?: it.value.defaultColumnName() }
            } else if (grouping.isNotEmpty()) {
                grouping.map { it.defaultColumnName() }
            } else {
                source.columnNames + joins.flatMap { it.data.columnNames }
            }

    override fun sql() = """
        SELECT ${columns?.joinToString(", ") { it.sql() } ?: "*"}
        FROM ${source.sql()}
        ${joins.joinToString("\n") { it.sql() }}
        ${if (filter != null) "WHERE ${filter.sql()}" else ""}
        ${if (grouping.isNotEmpty()) "GROUP BY ${grouping.joinToString(",") { it.sql() }}" else ""}
    """.trimIndent()

    override fun equals(other: Any?) = other is Select<*, *>
            && joins == other.joins
            && columns == other.columns
            && filter == other.filter
            && grouping == other.grouping

    override fun hashCode() = Objects.hash(source, joins, columns, filter, grouping)
}

inline fun <E : Engine<E>, reified T : Any> Select<E, T>.execute(connection: Connection<E>) =
    connection.execute(this, T::class)
