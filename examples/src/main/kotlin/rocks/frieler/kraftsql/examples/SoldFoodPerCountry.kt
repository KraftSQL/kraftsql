package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.Sale
import rocks.frieler.kraftsql.examples.data.Shop
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.examples.data.sales
import rocks.frieler.kraftsql.examples.data.shops
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.h2.queries.Select
import rocks.frieler.kraftsql.queries.Projection
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemorySession
import rocks.frieler.kraftsql.h2.queries.execute
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.queries.QuerySource
import rocks.frieler.kraftsql.objects.Row
import rocks.frieler.kraftsql.queries.InnerJoin
import rocks.frieler.kraftsql.testing.engine.SimulatorSession
import java.time.Instant

fun main() {
    if (System.getenv("SIMULATE_ENGINE").toBoolean()) {
        H2InMemorySession.Default.set(SimulatorSession())
    }

    products.create()
    val chocolate = Product(1, "Chocolate", "Food").also { it.insertInto(products) }
    val pants = Product(2, "Pants", "Clothes").also { it.insertInto(products) }

    shops.create()
    val shop1 = Shop(1, "DE").also { it.insertInto(shops) }
    val shop2 = Shop(2, "NL").also { it.insertInto(shops) }

    sales.create()
    Sale(chocolate, shop1, Instant.parse("2025-01-03T08:22:14+01:00"), 2).insertInto(sales)
    Sale(pants, shop1, Instant.parse("2025-01-03T08:22:14+01:00"), 1).insertInto(sales)
    Sale(chocolate, shop2, Instant.parse("2025-01-03T09:01:33+01:00"), 1).insertInto(sales)

    calculateSoldFoodPerCountry(products, shops, sales)
        .execute().forEach {
            println("${it[Shop::country.name]}: ${it["_totalAmount"]}")
        }
}

fun calculateSoldFoodPerCountry(
    products: Data<H2Engine, Product>,
    shops: Data<H2Engine, Shop>,
    sales: Data<H2Engine, Sale>
): Select<Row> {
    val p = QuerySource(products, "p")
    val s = QuerySource(shops, "s")
    val soldFoodPerCountry = Select<Row>(
        source = sales,
        joins = listOf(
            p.let { p -> InnerJoin(p, sales[Sale::productId] `=` p[Product::id]) },
            s.let { s -> InnerJoin(s, sales[Sale::storeId] `=` s[Shop::id]) },
        ),
        columns = listOf(
            Projection(s[Shop::country], Shop::country.name),
            Projection(Sum.Companion(sales[Sale::amount]), "_totalAmount"),
        ),
        filter = p[Product::category] `=` Constant("Food"),
        grouping = listOf(s[Shop::country]),
    )
    return soldFoodPerCountry
}
