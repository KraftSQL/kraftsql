package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.dql.Select
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.objects.Row
import rocks.frieler.kraftsql.dql.QuerySource

fun main() {
    Select<Row>(
        source = QuerySource(ConstantData(Row(mapOf("foo" to "bar", "foo" to "baz"))))
    )
        .execute()
        .also { println(it.count()) }
}
