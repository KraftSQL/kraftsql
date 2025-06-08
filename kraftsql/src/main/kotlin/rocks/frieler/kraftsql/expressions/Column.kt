package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine

class Column<E: Engine<E>, T>(
    private val modelAlias: String? = null,
    private val name: String,
) : Expression<E, T> {

    constructor(name: String) : this(null, name)

    override fun sql() = "${modelAlias?.let { "\"$modelAlias\"." } ?: ""}\"$name\""
}
