package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.Data

open class Select<E : Engine<E>, T : Any>(
    val source: QuerySource<E, *>,
    val joins: List<Join<E>> = emptyList(),
    val columns: List<Projection<E, *>>? = null,
    val filter: Expression<E, Boolean>? = null,
    val grouping: List<Expression<E, *>> = emptyList(),
) : Command<E, List<T>>, Data<E, T> {

    override fun sql() = """
        SELECT ${columns?.joinToString(", ") { it.sql() } ?: "*"}
        FROM ${source.sql()}
        ${joins.joinToString("\n") { it.sql() }}
        ${if (filter != null) "WHERE ${filter.sql()}" else ""}
        ${if (grouping.isNotEmpty()) "GROUP BY ${grouping.joinToString(",") { it.sql() }}" else ""}
    """.trimIndent()

    override fun inferSchema(): List<Column<E>> {
        if (joins.isNotEmpty()) {
            TODO("Not yet implemented")
        }
        if (grouping.isNotEmpty()) {
            TODO("Not yet implemented")
        }
        if (columns != null) {
            TODO("Not yet implemented")
        }
        return source.inferSchema()
    }
}

inline fun <E : Engine<E>, reified T : Any> Select<E, T>.execute(connection: Connection<E>) =
    connection.execute(this, T::class)
