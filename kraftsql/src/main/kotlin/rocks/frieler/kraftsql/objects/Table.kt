package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping
import kotlin.reflect.KClass

open class Table<E: Engine<E>, T : Any>(
    val database: String? = null,
    val schema: String? = null,
    val name: String,
    val columns: List<Column<E>>,
) : Data<E, T> {

    constructor(orm: ORMapping<E, *>, database: String?, schema: String?, name: String, type: KClass<T>)
            : this(database, schema, name, orm.getSchemaFor(type))

    val qualifiedName: String
        get() = listOfNotNull(database, schema, name).joinToString(".")

    override fun get(field: String): rocks.frieler.kraftsql.expressions.Column<E, Any?> {
        check(columns.any { it.name == field }) { "no column '${field}' in table '$name'" }
        return super.get(field)
    }

    override fun sql() =
        listOfNotNull(database, schema, name).joinToString(".") { part -> "`$part`" }
}
