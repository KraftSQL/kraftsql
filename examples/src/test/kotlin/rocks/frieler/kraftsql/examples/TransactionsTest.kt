package rocks.frieler.kraftsql.examples

import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.dml.execute
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.dsl.inTransaction
import rocks.frieler.kraftsql.h2.engine.Types.INTEGER
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.h2.testing.WithH2Simulator
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.matchers.collections.shouldContainExactlyOne

@WithH2Simulator
class TransactionsTest {
    @Test
    fun `exception leads to rollback which restores initial data`() {
        val table = Table<DataRow>(name = "something", columns = listOf(Column("number", INTEGER))).also {
            it.create()
        }
        DataRow(mapOf("number" to 1)).insertInto(table)
        DataRow(mapOf("number" to 2)).insertInto(table)

        try {
            inTransaction {
                Delete(table, table.get<Int>("number") `=` Constant(1)).execute()
                DataRow(mapOf("number" to 3, "foo" to "bar")).insertInto(table)
            }
        } catch (e: Exception) {
            println(e.message)

        } finally {
            Select<DataRow> {
                from(table)
                columns(table.get<Int>("number") `as` "number")
            }.execute().also { result ->
                result shouldContainExactlyOne { it["number"] == 1 }
                result shouldContainExactlyOne { it["number"] == 2 }
            }
        }
    }
}
