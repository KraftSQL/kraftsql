package rocks.frieler.kraftsql.queries

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.HasColumns

class QuerySource<E: Engine<E>, T : Any>(
    val data: Data<E, T>,
    val alias: String? = null,
) : HasColumns<E, T> {
    fun sql() = data.sql()
        .let { sql -> if (sql.contains(" ")) "($sql)" else sql }
        .let { sql -> if (alias != null) "$sql AS \"$alias\"" else sql }

    override operator fun <V> get(field: String) = super.get<V>(field)
        .let { if (alias != null) it.withQualifier(alias) else it }
}
