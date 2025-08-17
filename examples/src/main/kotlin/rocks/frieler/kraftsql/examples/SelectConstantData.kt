package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.dql.Select
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product

fun main() {
    Select<DataRow>(
        source = QuerySource(ConstantData(DataRow(mapOf("foo" to "bar", "fuu" to "baz"))))
    )
        .execute()
        .forEach { println(it) }

    Select<Product>(
        source = QuerySource(ConstantData(Product(1, "foo", Category(0, "bar"), tags = arrayOf("foo", "bar"))))
    )
        .execute()
        .forEach { println(it) }
}
