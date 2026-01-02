package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping
import rocks.frieler.kraftsql.expressions.Row

/**
 * Constant [Data].
 *
 * @param <E> the [Engine] where this [ConstantData] can be rendered as SQL and worked with
 * @param <T> the Kotlin type of the [ConstantData]'s items, either a data class or generically [DataRow]
 */
open class ConstantData<E : Engine<E>, T : Any> protected constructor(
    private val orm: ORMapping<E, *>,
    /**
     * The items - or rows - in this data.
     */
    val items: Iterable<T>,
    private val columnNames: List<String>,
) : Data<E, T> {

    /**
     * Creates a new [ConstantData] with the given items.
     *
     * The items must not be empty and all be of the same class, either a data-class or [DataRow].
     *
     * @param orm the [ORMapping] of the [Engine]
     * @param items the rows
     */
    constructor(orm: ORMapping<E, *>, items: Iterable<T>) : this(
        orm,
        items.apply {
            val iterator = items.iterator()
            require(iterator.hasNext()) { "ConstantData needs at least one item (or you must specify schema information)." }
            val referenceItem = iterator.next()
            iterator.forEachRemaining { item ->
                require(item::class == referenceItem::class) { "All items must be of the same type." }
                if (referenceItem is DataRow && item is DataRow) {
                    require(item.columnNames == referenceItem.columnNames) { "All DataRow items must have the same columns." }
                }
            }
        },
        items.first().let { item -> if (item is DataRow) item.columnNames else orm.getSchemaFor(item::class).map { it.name } }
    )

    constructor(orm: ORMapping<E, *>, vararg items: T) : this(orm, items.toList())

    protected constructor(orm: ORMapping<E, *>, columnNames: List<String>) : this(orm, emptyList(), columnNames)

    companion object {
        /**
         * Creates a new empty [ConstantData] with the given column names (although there are no rows).
         *
         * Note: This function is mainly intended for use with (no) [DataRow]s. In case of (no) instances of a
         * data-class, you should prefer the overloaded version that derives the column names from that class.
         *
         * @param E the [Engine] where this [ConstantData] can be rendered as SQL and worked with
         * @param <T> the Kotlin type of the [ConstantData]'s items, either a data class or generically [DataRow]
         */
        fun <E : Engine<E>, T : Any> empty(orm: ORMapping<E, *>, columnNames: List<String>) =
            ConstantData<E, T>(orm, columnNames)

        /**
         * Creates a new empty [ConstantData] deriving the column names from the row type (although there are no rows)
         * using [ORMapping.getSchemaFor].
         *
         * @param E the [Engine] where this [ConstantData] can be rendered as SQL and worked with
         * @param <T> the Kotlin type of the [ConstantData]'s items; must be a data class
         */
        inline fun <E : Engine<E>, reified T : Any> empty(orm: ORMapping<E, *>) =
            empty<E, T>(orm, orm.getSchemaFor(T::class).map { it.name })
    }

    override fun sql(): String {
        return if (items.none()) {
            "SELECT ${columnNames.joinToString(", ") { column -> "NULL AS `$column`" }} WHERE FALSE"
        } else {
            items.joinToString(separator = " UNION ALL ") { item ->
                val serializedItem = orm.serialize(item)
                if (serializedItem is Row && serializedItem.values != null) {
                    "SELECT ${serializedItem.values.entries.joinToString(", ") { (name, expression) -> "${expression.sql()} AS `$name`" }}"
                } else {
                    error("Item was serialized to something else than Row; this is illegal.")
                }
            }
        }
    }
}
