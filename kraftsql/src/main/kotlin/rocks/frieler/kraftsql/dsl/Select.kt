package rocks.frieler.kraftsql.dsl

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.HasColumns
import rocks.frieler.kraftsql.queries.InnerJoin
import rocks.frieler.kraftsql.queries.Join
import rocks.frieler.kraftsql.queries.Projection
import rocks.frieler.kraftsql.queries.QuerySource
import rocks.frieler.kraftsql.queries.Select

fun <E : Engine<E>, T : Any> Select(configurator: @SqlDsl SelectBuilder<E, T>.() -> Unit) : Select<E, T> {
    return SelectBuilder<E, T>().apply { configurator() }.build()
}

@SqlDsl
open class SelectBuilder<E : Engine<E>, T : Any> {
    private lateinit var source: QuerySource<E, *>
    private val joins: MutableList<Join<E>> = mutableListOf()
    private val columns: MutableList<Projection<E, *>> = mutableListOf()
    private lateinit var filter: Expression<E, Boolean>
    private val grouping: MutableList<Expression<E, *>> = mutableListOf()

    fun <S: Any> from(source: QuerySource<E, S>) : HasColumns<E, S> {
        check(!this::source.isInitialized) { "SELECT already has a source to select from." }
        return source
            .also { this.source = it }
    }

    fun from(source: Data<E, *>) = from(QuerySource(source))

    fun columns(vararg columns: Projection<E, *>) {
        this.columns.addAll(columns)
    }

    fun <J : Any> innerJoin(data: QuerySource<E, J>, condition: @SqlDsl QuerySource<E, J>.() -> Expression<E, Boolean>) : HasColumns<E, J> {
        return data
            .also { joins.add(InnerJoin(it, condition(data))) }
    }

    fun <J : Any> innerJoin(data: Data<E, J>, condition: @SqlDsl (QuerySource<E, J>) -> Expression<E, Boolean>) = innerJoin(QuerySource(data), condition)

    fun where(filter: Expression<E, Boolean>) {
        check(!::filter.isInitialized) { "SELECT already has a WHERE-filter." }
        this.filter = filter
    }

    fun groupBy(vararg columns: Expression<E, *>) {
        grouping.addAll(columns)
    }

    fun build(): Select<E, T> = Select(
        source,
        joins,
        columns.takeIf { it.isNotEmpty() },
        if (::filter.isInitialized) filter else null,
        grouping,
    )
}

infix fun <E : Engine<E>, T : Any> Data<E, T>.`as`(alias: String) = QuerySource(this, alias)

infix fun <E : Engine<E>, T> Expression<E, T>.`as`(alias: String) = Projection(this, alias)
