package rocks.frieler.kraftsql.h2.queries

import rocks.frieler.kraftsql.h2.engine.H2InMemoryEngine
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.models.Model
import rocks.frieler.kraftsql.models.Row

class Select<T : Any> : rocks.frieler.kraftsql.queries.Select<H2InMemoryEngine, T> {
    constructor(from: Model<H2InMemoryEngine, T>, columns: List<ColumnExpression<H2InMemoryEngine, *>>? = null) : super(from, columns)

    companion object {
        operator fun invoke(from: Model<H2InMemoryEngine, *>, columns: List<ColumnExpression<H2InMemoryEngine, *>>? = null) = Select<Row>(from.asRows(), columns)
    }
}
