package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow

fun main() {
    Select<DataRow> {
        from(QuerySource(ConstantData(DataRow(mapOf("foo" to "bar", "fuu" to "baz")))))
        columns(Array(arrayOf(Column<H2Engine, String>("foo"), Column("fuu"))) `as` "strings")
    }
        .execute()
        .forEach {
            @Suppress("UNCHECKED_CAST")
            println((it["strings"] as kotlin.Array<String>).contentToString())
        }
}
