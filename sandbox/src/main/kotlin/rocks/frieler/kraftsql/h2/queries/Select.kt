package rocks.frieler.kraftsql.h2.queries

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.models.Model

class Select<T : Any> : rocks.frieler.kraftsql.queries.Select<H2Engine, T> {
    constructor(from: Model<H2Engine, *>, columns: List<ColumnExpression<H2Engine, *>>? = null) : super(from, columns)
}
