package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Expression

abstract class Join<E : Engine<E>>(
    val data: QuerySource<E, *>,
    val condition: Expression<E, Boolean>,
) {
    abstract fun sql(): String
}

class InnerJoin<E : Engine<E>>(
    data: QuerySource<E, *>,
    condition: Expression<E, Boolean>,
) : Join<E>(data, condition) {
    override fun sql() = "INNER JOIN ${data.sql()} ON ${condition.sql()}"
}
