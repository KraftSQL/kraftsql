package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.commands.Command
import rocks.frieler.kraftsql.engine.Session
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Data

open class Select<E : Engine<E>, T : Any>(
    val source: Data<E, *>,
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
}

inline fun <E : Engine<E>, reified T : Any> Select<E, T>.execute(session: Session<E>) =
    session.execute(this, T::class)
