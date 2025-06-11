package rocks.frieler.kraftsql.h2.queries

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.queries.Join
import rocks.frieler.kraftsql.queries.Queryable
import rocks.frieler.kraftsql.queries.Selectable

class Select<T : Any> : rocks.frieler.kraftsql.queries.Select<H2Engine, T> {
    constructor(
        source: Queryable<H2Engine>,
        joins: List<Join<H2Engine>> = emptyList(),
        columns: List<Selectable<H2Engine>>? = null,
        filter: Expression<H2Engine, Boolean>? = null,
        grouping: List<Expression<H2Engine, *>> = emptyList(),
    ) : super(source, joins, columns, filter, grouping)
}
