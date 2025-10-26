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

    override fun get(field: String) = Column<E, Any?>(qualifiers + name, field)

    open fun withQualifier(qualifier: String) = Column<E, T>(listOf(qualifier) + qualifiers, name)

    override fun equals(other: Any?) = other is Column<*, *> && qualifiedName == other.qualifiedName

    override fun hashCode(): Int = qualifiedName.hashCode()
}
