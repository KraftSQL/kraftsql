package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.h2.engine.H2Engine

class Column<T>(val parent: Column<*>?, qualifiers: List<String>, name: String) : rocks.frieler.kraftsql.expressions.Column<H2Engine, T>(qualifiers, name) {

    constructor(qualifiers: List<String>, name: String) : this(null, qualifiers, name)

    constructor(parent: Column<*>, name: String) : this(parent, emptyList(), name)

    constructor(name: String) : this(null, emptyList(), name)

    override val qualifiedName: String
        get() = super.qualifiedName.let { if (parent != null) "${parent.qualifiedName}.$it" else it }

    override fun sql(): String {
        var sql = "`$name`"
        if (qualifiers.isNotEmpty()) sql = "${qualifiers.joinToString(".") { "`$it`" }}.$sql"
        if (parent != null) sql = "(${parent.sql()}).$sql"
        return sql
    }

    override fun get(column: String) = Column<Any?>(this, column)

    override fun withQualifier(qualifier: String) = super.withQualifier(qualifier).let { Column<T>(it.qualifiers, it.name) }
}
