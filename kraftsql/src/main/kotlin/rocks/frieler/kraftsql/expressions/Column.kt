package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.HasColumns

open class Column<E: Engine<E>, T : Any>(
    val qualifiers: List<String>,
    val name: String,
) : Expression<E, T>, HasColumns<E, T> {

    constructor(name: String) : this(emptyList(), name)

    open val qualifiedName: String
        get() = "${qualifiers.joinToString("") { "$it." }}$name"

    override fun sql(): String = "${qualifiers.joinToString("") { "`$it`." }}`$name`"

    override fun defaultColumnName() = qualifiedName

    override fun <V : Any> get(field: String) = Column<E, V>(qualifiers + name, field)

    open fun withQualifier(qualifier: String) = Column<E, T>(listOf(qualifier) + qualifiers, name)

    override fun equals(other: Any?) = other is Column<*, *> && qualifiedName == other.qualifiedName

    override fun hashCode(): Int = qualifiedName.hashCode()
}
