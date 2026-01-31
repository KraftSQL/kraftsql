package rocks.frieler.kraftsql.h2.dsl

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.dsl.SqlDsl
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.dql.QuerySource
import rocks.frieler.kraftsql.h2.dql.Select
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.HasColumns

fun <T : Any> Select(configurator: @SqlDsl SelectBuilder<T>.() -> Unit) : Select<T> =
    SelectBuilder<T>().apply(configurator).build()

class SelectBuilder<T : Any> : rocks.frieler.kraftsql.dsl.SelectBuilder<H2Engine, T>() {
    override fun <S : Any> from(source: rocks.frieler.kraftsql.dql.QuerySource<H2Engine, S>): HasColumns<H2Engine, S> {
        require(source is QuerySource) { "h2 requires its own QuerySource implementation." }
        return super.from(source)
    }

    override fun <S : Any> from(source: Data<S>): HasColumns<H2Engine, S> {
        return super.from(QuerySource(source))
    }

    override fun <J : Any> innerJoin(
        data: rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>,
        condition: @SqlDsl rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>.() -> Expression<H2Engine, Boolean?>,
    ): HasColumns<H2Engine, J> {
        require(data is QuerySource) { "h2 requires its own QuerySource implementation." }
        return super.innerJoin(data, condition)
    }

    override fun <J : Any> innerJoin(
        data: Data<J>,
        condition: @SqlDsl rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>.() -> Expression<H2Engine, Boolean?>,
    ): HasColumns<H2Engine, J> {
        return super.innerJoin(QuerySource(data), condition)
    }

    override fun <J : Any> leftJoin(
        data: rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>,
        condition: @SqlDsl rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>.() -> Expression<H2Engine, Boolean?>,
    ): HasColumns<H2Engine, J> {
        require(data is QuerySource) { "h2 requires its own QuerySource implementation." }
        return super.leftJoin(data, condition)
    }

    override fun <J : Any> leftJoin(
        data: Data<J>,
        condition: @SqlDsl rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>.() -> Expression<H2Engine, Boolean?>,
    ): HasColumns<H2Engine, J> {
        return super.leftJoin(QuerySource(data), condition)
    }

    override fun <J : Any> rightJoin(
        data: rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>,
        condition: @SqlDsl rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>.() -> Expression<H2Engine, Boolean?>,
    ): HasColumns<H2Engine, J> {
        require(data is QuerySource) { "h2 requires its own QuerySource implementation." }
        return super.rightJoin(data, condition)
    }

    override fun <J : Any> rightJoin(
        data: Data<J>,
        condition: @SqlDsl rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>.() -> Expression<H2Engine, Boolean?>,
    ): HasColumns<H2Engine, J> {
        return super.rightJoin(QuerySource(data), condition)
    }

    override fun <J : Any> crossJoin(data: rocks.frieler.kraftsql.dql.QuerySource<H2Engine, J>): HasColumns<H2Engine, J> {
        require(data is QuerySource) { "h2 requires its own QuerySource implementation." }
        return super.crossJoin(data)
    }

    override fun <J : Any> crossJoin(data: Data<J>) = super.crossJoin(QuerySource(data))

    override fun build(): Select<T> {
        val pureSelect = super.build()
        return Select(pureSelect.source, pureSelect.joins, pureSelect.columns, pureSelect.filter, pureSelect.grouping)
    }
}

infix fun <T : Any> Data<T>.`as`(alias: String) = QuerySource(this, alias)
