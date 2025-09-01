package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.examples.data.with
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.h2.dml.execute
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.Types.INTEGER
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.DataRow

fun main() {
    val table = Table<DataRow>(name = "something", columns = listOf(Column("number", INTEGER)))
    with(table) {
        DataRow(mapOf("number" to 1)).insertInto(table)
        DataRow(mapOf("number" to 2)).insertInto(table)

        try {
            rocks.frieler.kraftsql.h2.dsl.inTransaction {
                Delete(table, table.get<Int>("number") `=` Constant(1)).execute()
                DataRow(mapOf("number" to 3, "foo" to "bar")).insertInto(table)
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            Select<DataRow> { from(table) }.execute()
                .forEach { println(it) }
        }
    }
}
