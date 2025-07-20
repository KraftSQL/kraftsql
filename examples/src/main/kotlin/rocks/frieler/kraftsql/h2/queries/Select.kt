package rocks.frieler.kraftsql.h2.queries

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection
import rocks.frieler.kraftsql.queries.Join
import rocks.frieler.kraftsql.queries.Projection
import rocks.frieler.kraftsql.queries.QuerySource
import rocks.frieler.kraftsql.queries.Select
import rocks.frieler.kraftsql.queries.execute

class Select<T : Any> : Select<H2Engine, T> {
    constructor(
        source: QuerySource<H2Engine, *>,
        joins: List<Join<H2Engine>> = emptyList(),
        columns: List<Projection<H2Engine, *>>? = null,
        filter: Expression<H2Engine, Boolean>? = null,
        grouping: List<Expression<H2Engine, *>> = emptyList(),
    ) : super(source, joins, columns, filter, grouping)
}

inline fun <reified T : Any> Select<H2Engine, T>.execute() =
    execute(H2InMemoryConnection.Default.get())
