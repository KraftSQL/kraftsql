package rocks.frieler.kraftsql.h2.dql

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.dql.Join
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.dql.execute

class Select<T : Any> : Select<H2Engine, T> {
    constructor(
        source: QuerySource<H2Engine, *>,
        joins: List<Join<H2Engine>> = emptyList(),
        columns: List<Projection<H2Engine, *>>? = null,
        filter: Expression<H2Engine, Boolean?>? = null,
        grouping: List<Expression<H2Engine, *>> = emptyList(),
    ) : super(source, joins, columns, filter, grouping)
}

inline fun <reified T : Any> Select<H2Engine, T>.execute() =
    execute(H2Engine.DefaultConnection.get())
