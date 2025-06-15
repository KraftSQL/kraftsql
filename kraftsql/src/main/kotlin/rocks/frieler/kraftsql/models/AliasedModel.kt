package rocks.frieler.kraftsql.models

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.queries.Queryable

class AliasedModel<E: Engine<E>, T : Any>(
    private val model: Model<E, T>,
    private val alias: String,
) : Queryable<E>, HasColumns<E, T> {
    override fun sql() = "(${model.sql()}) AS \"${alias}\""

    override operator fun <V> get(field: String) = Column<E, V>(alias, field)
}
