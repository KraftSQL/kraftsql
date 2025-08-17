package rocks.frieler.kraftsql.h2.dsl

import rocks.frieler.kraftsql.dsl.SelectBuilder
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.dsl.Select
import rocks.frieler.kraftsql.dsl.SqlDsl
import rocks.frieler.kraftsql.h2.dql.QuerySource
import rocks.frieler.kraftsql.objects.Data

fun <T : Any> Select(configurator: @SqlDsl SelectBuilder<H2Engine, T>.() -> Unit) = Select<H2Engine, T>(configurator)

infix fun <T : Any> Data<H2Engine, T>.`as`(alias: String) = QuerySource(this, alias)
