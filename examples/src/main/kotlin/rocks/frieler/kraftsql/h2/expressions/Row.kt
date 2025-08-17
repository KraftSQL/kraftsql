package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.h2.engine.H2Engine

class Row<T : Any>(values: Map<String, Expression<H2Engine, *>>?) : Row<H2Engine, T>(values) {
    override fun sql(): String {
        if (values == null) {
            return "NULL"
        }
        return "(${values!!.values.joinToString(", ") { value -> value.sql() }})"
    }

    override fun defaultColumnName(): String {
        if (values == null) {
            return "NULL"
        }
        return values!!.values.joinToString(",") { value -> value.defaultColumnName() }
    }
}
