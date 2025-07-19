package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.queries.Select
import rocks.frieler.kraftsql.h2.queries.execute
import rocks.frieler.kraftsql.objects.Row
import rocks.frieler.kraftsql.queries.QuerySource

fun main() {
    Select<Row>(
        source = QuerySource(ConstantData(Row(mapOf("foo" to "bar", "foo" to "baz"))))
    )
        .execute()
        .also { println(it.count()) }
}
