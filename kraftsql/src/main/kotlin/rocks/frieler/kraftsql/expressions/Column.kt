package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Column<E: Engine<E>, T>(
    private val qualifiers: List<String>,
    private val name: String,
) : Expression<E, T> {

    constructor(name: String) : this(emptyList(), name)

    override fun sql() = "${qualifiers.joinToString { "\"$it\"." }}\"$name\""

    fun withQualifier(qualifier: String) = Column<E, T>(listOf(qualifier) + qualifiers, name)
}
