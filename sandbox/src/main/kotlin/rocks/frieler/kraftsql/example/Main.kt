package rocks.frieler.kraftsql.example

import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.queries.Select
import rocks.frieler.kraftsql.queries.Projection
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.engine.H2InMemorySession
import rocks.frieler.kraftsql.h2.objects.Table
import rocks.frieler.kraftsql.h2.queries.execute
import rocks.frieler.kraftsql.queries.QuerySource
import rocks.frieler.kraftsql.objects.Row
import rocks.frieler.kraftsql.queries.InnerJoin
import rocks.frieler.kraftsql.testing.engine.SimulatorSession
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun main() {
    if (System.getenv("SIMULATE_ENGINE").toBoolean()) {
        H2InMemorySession.Default.set(SimulatorSession())
    }

    val chocolate = Product(1, "Chocolate", "Food")
    val pants = Product(2, "Pants", "Clothes")
    val products = Table("product", Product::class).apply {
        create()
        ConstantData(
            chocolate, pants,
        ).insertInto(this)
    }

    val store1 = Store(1, "DE")
    val store2 = Store(2, "NL")
    val stores = Table("store", Store::class).apply {
        create()
        ConstantData(
            store1,
            store2,
        ).insertInto(this)
    }

    val sales = Table("sales", Sale::class).apply {
        create()
        ConstantData(
            Sale(chocolate, store1, Instant.from(ZonedDateTime.of(LocalDateTime.of(2025, 1, 3, 8, 22, 14), ZoneId.of("CET"))), 2),
            Sale(pants, store1, Instant.from(ZonedDateTime.of(LocalDateTime.of(2025, 1, 3, 8, 22, 14), ZoneId.of("CET"))), 1),
            Sale(chocolate, store2, Instant.from(ZonedDateTime.of(LocalDateTime.of(2025, 1, 3, 9, 1, 33), ZoneId.of("CET"))), 1),
        ).insertInto(this)
    }

    val p = QuerySource(products, "p")
    val s = QuerySource(stores, "s")
    Select<Row>(
        source = sales,
        joins = listOf(
            p.let { p -> InnerJoin(p, sales[Sale::productId] `=` p[Product::id]) },
            s.let { s -> InnerJoin(s, sales[Sale::storeId] `=` s[Store::id]) },
        ),
        columns = listOf(
            Projection(s[Store::country], Store::country.name),
            Projection(Sum(sales[Sale::amount]), "_totalAmount"),
        ),
        filter = p[Product::category] `=` Constant("Food"),
        grouping = listOf(s[Store::country]),
    ).execute().forEach { println("${it[Store::country.name]}: ${it["_totalAmount"]}") }
}
