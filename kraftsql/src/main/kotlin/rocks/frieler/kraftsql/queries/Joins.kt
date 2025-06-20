package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression

abstract class Join<E : Engine<E>>(
    val data: Queryable<E>,
    val condition: Expression<E, Boolean>,
) {
    abstract fun sql(): String
}

class InnerJoin<E : Engine<E>>(
    data: Queryable<E>,
    condition: Expression<E, Boolean>,
) : Join<E>(data, condition) {
    override fun sql() = "INNER JOIN ${data.sql()} ON ${condition.sql()}"
}
