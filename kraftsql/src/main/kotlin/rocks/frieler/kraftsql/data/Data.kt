package rocks.frieler.kraftsql.data

import rocks.frieler.kraftsql.expressions.Column
import kotlin.reflect.KProperty1

/**
 * Base interface for all representations of structured, tabular data.
 *
 * @param T the Kotlin type of the [Data]'s rows
 */
interface Data<T : Any> {
    val schema: List<rocks.frieler.kraftsql.data.Column>

    /**
     * Retrieves a [Column] expression for the named column of this [Data].
     *
     * The caller specifies the Kotlin type [V] of the column's values, e.g. `data.get<String?>("name")`. It is not
     * checked against the column's [Type] in the [schema], so the caller must take care that it matches.
     *
     * @param V the Kotlin type of the column's values
     * @param column the name of the column
     * @return a [Column] expression for the named column
     * @throws IllegalArgumentException if there is no column with the given name in the [schema]
     */
    operator fun <V> get(column: String) = Column<V>(this, column)

    /**
     * Retrieves a [Column] expression for the column specified by a [property][KProperty1] of the rows' type.
     *
     * The column is identified by the [property][KProperty1]'s name. Its Kotlin type determines the type of the
     * [Column] expression.
     *
     * @param V the Kotlin type of the property and thereby of the column's values
     * @param property the [property][KProperty1] of the rows' type to get the [Column] for
     * @return a [Column] expression for the specified [property][KProperty1]
     * @throws IllegalArgumentException if there is no column with the given name and type in the [schema]
     */
    operator fun <V> get(property: KProperty1<T, V>) = Column<V>(this, property.name).apply {
        // Type.get() throws for unsupported Kotlin types, so check the name first, by the Column's construction.
        require(type == Type.get(property.returnType)) { "Column '${property.name}' is of type $type, not ${property.returnType}." }
    }
}
