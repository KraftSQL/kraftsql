package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Column<E: Engine<E>, T>(
    val qualifiers: List<String>,
    val name: String,
) : Expression<E, T> {

    constructor(name: String) : this(emptyList(), name)

    val qualifiedName: String
        get() = "${qualifiers.joinToString { "$it." }}$name"

    override fun sql() = "${qualifiers.joinToString { "`$it`." }}`$name`"

    override fun defaultColumnName() = qualifiedName

    fun withQualifier(qualifier: String) = Column<E, T>(listOf(qualifier) + qualifiers, name)

    override fun equals(other: Any?) = other is Column<*, *> && qualifiedName == other.qualifiedName

    override fun hashCode(): Int = qualifiedName.hashCode()
}
