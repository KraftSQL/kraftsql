package rocks.frieler.kraftsql.example

import rocks.frieler.kraftsql.ddl.create
import rocks.frieler.kraftsql.dml.insertInto
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.h2.models.ConstantModel
import rocks.frieler.kraftsql.h2.queries.Select
import rocks.frieler.kraftsql.expressions.ColumnExpression
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.models.Row
import rocks.frieler.kraftsql.queries.execute
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun main() {
    val chocolate = Product(1, "Chocolate", "Food")
    val pants = Product(2, "Pants", "Clothes")
    val products = Table("product", Product::class).apply {
        create()
        ConstantModel(
            chocolate, pants,
        ).insertInto(this)
    }

    val store1 = Store(1, "DE")
    val stores = Table("store", Store::class).apply {
        create()
        ConstantModel(
            store1,
        ).insertInto(this)
    }

    val sales = Table("sales", Sale::class).apply {
        create()
        ConstantModel(
            Sale(chocolate, store1, Instant.from(ZonedDateTime.of(LocalDateTime.of(2025, 1, 3, 8, 22, 14), ZoneId.of("CET"))), 2),
            Sale(pants, store1, Instant.from(ZonedDateTime.of(LocalDateTime.of(2025, 1, 3, 8, 22, 14), ZoneId.of("CET"))), 1),
        ).insertInto(this)
    }

    val totalAmount = Select<Row>(
        from = sales,
        columns = listOf(ColumnExpression(Sum(sales[Sale::amount]), "_totalAmount")),
    ).execute().single()["_totalAmount"]
    println(totalAmount)
}
