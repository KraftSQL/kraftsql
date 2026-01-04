package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.HasColumns

/**
 * An [Expression] that references a column in the data.
 *
 * @param <E> the [Engine] targeted
 * @param <T> the Kotlin type of the [Column]
 * @param qualifiers optional qualifiers for the [Column], e.g., the table name or alias
 * @param name the bare name of the [Column]
 */
open class Column<E: Engine<E>, T>(
    val qualifiers: List<String>,
    val name: String,
) : Expression<E, T>, HasColumns<E, T> {

    constructor(name: String) : this(emptyList(), name)

    open val qualifiedName: String
        get() = "${qualifiers.joinToString("") { "$it." }}$name"

    override fun sql(): String = "${qualifiers.joinToString("") { "`$it`." }}`$name`"

    override fun defaultColumnName() = qualifiedName

    /**
     * WARNING: Not implemented!
     *
     * In case this [Column] has a structured type with sub-columns, this should provide the names of those.
     * Unfortunately, the [Column] expression holds no information about the referenced column and its type. This
     * information is contextual, as it depends on the data this expression is evaluated against. Explore that data's
     * schema, if you need this kind of information.
     *
     * @throws NotImplementedError ALWAYS!
     */
    override val columnNames: List<String>
        get() = throw NotImplementedError("Column names of possibly structured columns are not yet supported.")

    /**
     * Retrieves a [Column] expression for the named sub-column, assuming that this [Column] has a structured type.
     *
     * This [Column]s full qualified name is added to the [Column] expression, but must not be part of the given column
     * name.
     *
     * @param column the name of the sub-column
     * @return a [Column] expression for the named sub-column
     */
    override fun get(column: String) = Column<E, Any?>(qualifiers + name, column)

    open fun withQualifier(qualifier: String) = Column<E, T>(listOf(qualifier) + qualifiers, name)

    override fun equals(other: Any?) = other is Column<*, *> && qualifiedName == other.qualifiedName

    override fun hashCode(): Int = qualifiedName.hashCode()
}
