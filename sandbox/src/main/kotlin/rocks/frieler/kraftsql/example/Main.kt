package rocks.frieler.kraftsql.example

import rocks.frieler.kraftsql.ddl.create
import rocks.frieler.kraftsql.dml.insertInto
import rocks.frieler.kraftsql.h2.models.ConstantModel
import rocks.frieler.kraftsql.h2.queries.Select
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.models.Row
import rocks.frieler.kraftsql.queries.execute

fun main() {
    val products = Table("product", Product::class)
        .apply { create() }

    ConstantModel(
        Product(1, "Chocolate", "Food"),
        Product(2, "Pants", "Clothes"),
    )
        .apply { insertInto(products) }

    val count = Select<Row>(
        from = products,
        columns = listOf(ColumnExpression(Count(), "_count")),
    ).execute().single()["_count"]
    println(count)
}
