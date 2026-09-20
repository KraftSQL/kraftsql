package rocks.frieler.kraftsql.data

import rocks.frieler.kraftsql.engine.DataClassSerDe

/**
 * Constant [Data].
 *
 * @param T the Kotlin type of the [ConstantData]'s items, either a data class or generically [DataRow]
 */
open class ConstantData<T : Any> protected constructor(
    /**
     * The items - or rows - in this data.
     */
    val items: Iterable<T>,
    override val schema: List<Column>,
) : Data<T> {

    /**
     * Creates a new [ConstantData] with the given items.
     *
     * The items must not be empty and all be of the same type, either a data-class or [DataRow].
     *
     * @param items the rows
     */
    constructor(items: Iterable<T>) : this(
        items.apply {
            val iterator = items.iterator()
            require(iterator.hasNext()) { "ConstantData needs at least one item (or you must specify schema information)." }
            val referenceItem = iterator.next()
            iterator.forEachRemaining { item ->
                require(item::class == referenceItem::class) { "All items must be of the same type." }
                if (referenceItem is DataRow && item is DataRow) {
                    require(item.fieldNames == referenceItem.fieldNames) { "All DataRow items must have the same columns." }
                }
            }
        },
        // FIXME: Handle empty Data.
        items.first().let { item -> // FIXME: Look at all items.
            if (item is DataRow) item.entries.map { (name, value) -> Column(name, Type.get(value!!::class)) } // FIXME: Watch for null.
            else if (item::class.isData) DataClassSerDe.getSchemaFor(item::class)
            else throw TODO()
        }
    )

    constructor(vararg items: T) : this(items.toList())
}
