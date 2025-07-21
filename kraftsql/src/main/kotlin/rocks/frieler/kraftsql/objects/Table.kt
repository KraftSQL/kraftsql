package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

open class Table<E: Engine<E>, T : Any>(
    val database: String? = null,
    val schema: String? = null,
    val name: String,
    val columns: List<Column<E>>,
) : Data<E, T> {

    constructor(engine: E, database: String?, schema: String?, name: String, type: KClass<T>) : this(
        database,
        schema,
        name,
        type.memberProperties.map { field ->
            Column<E>(field.name, engine.getTypeFor(field.returnType))
        }
    )

    val qualifiedName: String
        get() = listOfNotNull(database, schema, name).joinToString(".")

    override fun <V> get(field: String): rocks.frieler.kraftsql.expressions.Column<E, V> {
        check(columns.any { it.name == field }) { "no column '${field}' in table '$name'" }
        return super.get(field)
    }

    override fun sql() =
        listOfNotNull(database, schema, name).joinToString(".") { part -> "`$part`" }
}
