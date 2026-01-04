package rocks.frieler.kraftsql.objects

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import kotlin.reflect.KProperty1

/**
 * Interface for data objects that have [rocks.frieler.kraftsql.objects.Column]s which are referenceable by [Column]
 * expressions.
 *
 * @param E the [Engine] of the database object
 * @param T the Kotlin type of the database object's rows
 */
interface HasColumns<E : Engine<E>, T> {

    /**
     * The names of the available [rocks.frieler.kraftsql.objects.Column]s.
     */
    val columnNames: List<String>

    /**
     * Retrieves a [Column] expression for the named column.
     *
     * The name must be in the available [columnNames].
     *
     * If the data object adds any qualifier, such as an alias, to the columns, this qualifier is added to the [Column]
     * expression, but must not be part of the given column name.
     *
     * @param column the name of the column
     * @return a [Column] expression for the named column
     */
    operator fun get(column: String) = Column<E, Any?>(column.also { require(column in columnNames) })

    /**
     * Retrieves a [Column] expression for the column specified by a [property][KProperty1] of the rows' type.
     *
     * If the database object adds any qualifier, such as an alias, to the columns, this qualifier is added to the
     * [Column] expression.
     *
     * @param property the [property][KProperty1] of the rows' type to get the [Column] for
     * @return a [Column] expression for the specified [property][KProperty1]
     */
    operator fun <V> get(property: KProperty1<T, V>) : Column<E, V> {
        @Suppress("UNCHECKED_CAST")
        return this[property.name] as Column<E, V>
    }
}
