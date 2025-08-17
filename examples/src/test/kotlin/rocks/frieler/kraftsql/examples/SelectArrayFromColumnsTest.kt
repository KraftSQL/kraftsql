package rocks.frieler.kraftsql.examples

import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.h2.WithH2Simulator

@WithH2Simulator
class SelectArrayFromColumnsTest {
    @Test
    fun `Array has type based on elements`() {
        Select<DataRow> {
            from(QuerySource(ConstantData(DataRow(mapOf("foo" to "bar", "fuu" to "baz")))))
            columns(Array(arrayOf(Column<H2Engine, String>("foo"), Column("fuu"))) `as` "strings")
        }
            .execute()
            .forEach { // check if cast works:
                @Suppress("UNCHECKED_CAST")
                it["strings"] as kotlin.Array<String>
            }
    }
}
