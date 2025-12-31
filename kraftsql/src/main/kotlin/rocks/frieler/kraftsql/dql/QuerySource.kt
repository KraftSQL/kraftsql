package rocks.frieler.kraftsql.dql

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.HasColumns
import rocks.frieler.kraftsql.objects.HasSchema

open class QuerySource<E: Engine<E>, T : Any>(
    val data: Data<E, T>,
    val alias: String? = null,
) : HasSchema<E>, HasColumns<E, T> {
    fun sql() = data.sql()
        .let { sql -> if (sql.contains(" ")) "($sql)" else sql }
        .let { sql -> if (alias != null) "$sql AS `$alias`" else sql }

    override fun inferSchema() =
        data.inferSchema().map { if (alias == null) it else Column("$alias.${it.name}", it.type, it.nullable) }

    override operator fun get(field: String) = super.get(field)
        .let { if (alias != null) it.withQualifier(alias) else it }
}
