package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping
import kotlin.reflect.KClass

/**
 * A database [Table].
 *
 * @param E the [Engine] where this [Table] exists
 * @param T the Kotlin type of the [Table]'s rows
 */
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

    /**
     * The names of this [Table]'s [Column]s.
     */
    override val columnNames: List<String>
        get() = columns.map { it.name }

    /**
     * Retrieves a [rocks.frieler.kraftsql.expressions.Column] expression for the named column.
     *
     * The [Column] must exist in this [Table].
     *
     * @param column the name of the column
     * @return a [rocks.frieler.kraftsql.expressions.Column] expression for the named column
     */
    override fun get(column: String): rocks.frieler.kraftsql.expressions.Column<E, Any?> {
        check(columns.any { it.name == column }) { "no column '${column}' in table '$name'" }
        return super.get(column)
    }

    override fun sql() =
        listOfNotNull(database, schema, name).joinToString(".") { part -> "`$part`" }
}
