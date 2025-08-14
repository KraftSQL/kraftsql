package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.dql.Select
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.dql.QuerySource

fun main() {
    Select<DataRow>(
        source = QuerySource(ConstantData(DataRow(mapOf("foo" to "bar", "fuu" to "baz"))))
    )
        .execute()
        .forEach { println(it) }
}
