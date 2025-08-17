package rocks.frieler.kraftsql.h2.dql

import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.objects.Data

class QuerySource<T : Any>(data: Data<H2Engine, T>, alias: String? = null) : QuerySource<H2Engine, T>(data, alias) {
    override operator fun <V : Any> get(field: String) = Column<V>(listOfNotNull(alias), field)
}
