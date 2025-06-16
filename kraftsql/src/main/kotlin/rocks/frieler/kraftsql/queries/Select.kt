package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Session
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.models.Model
import rocks.frieler.kraftsql.models.Row
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

open class Select<E : Engine<E>, T : Any>(
    val source: Queryable<E>,
    val joins: List<Join<E>> = emptyList(),
    val columns: List<Selectable<E>>? = null,
    val filter: Expression<E, Boolean>? = null,
    val grouping: List<Expression<E, *>> = emptyList(),
) : Model<E, T>() {

    override fun sql() = """
        SELECT ${columns?.joinToString(", ") { it.sql() } ?: "*"}
        FROM ${source.sql()}
        ${joins.joinToString("\n") { it.sql() }}
        ${if (filter != null) "WHERE ${filter.sql()}" else ""}
        ${if (grouping.isNotEmpty()) "GROUP BY ${grouping.joinToString(",") { it.sql() }}" else ""}
    """.trimIndent()

    fun execute(session: Session<E>, type: KClass<T>) = session.execute(this, type)
}

inline fun <E : Engine<E>, reified T : Any> Select<E, T>.execute(session: Session<E>) = execute(session, T::class)
