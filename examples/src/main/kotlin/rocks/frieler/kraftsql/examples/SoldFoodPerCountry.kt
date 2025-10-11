package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.PurchaseItem
import rocks.frieler.kraftsql.examples.data.Customer
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.examples.data.purchases
import rocks.frieler.kraftsql.examples.data.customers
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Country
import rocks.frieler.kraftsql.examples.data.ProductOutline
import rocks.frieler.kraftsql.examples.data.Purchase
import rocks.frieler.kraftsql.examples.data.purchaseItems
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.h2.dsl.`as`
import rocks.frieler.kraftsql.h2.expressions.Constant

fun main() {
    withSampleData {
        calculateSoldFoodPerCountry(products, customers, purchases, purchaseItems)
            .execute().forEach {
                println("${it[Customer::country.name]}: ${it["_totalAmount"]}")
            }
    }
}

fun calculateSoldFoodPerCountry(
    products: Data<H2Engine, Product>,
    customers: Data<H2Engine, Customer>,
    purchases: Data<H2Engine, Purchase>,
    purchaseItems: Data<H2Engine, PurchaseItem>,
) = Select<DataRow> {
    from(purchaseItems)
    val purchases = innerJoin(purchases `as` "purchases") { this[Purchase::id] `=` purchaseItems[PurchaseItem::purchaseId] }
    val products = innerJoin(products `as` "products") { this[Product::id] `=` purchaseItems[PurchaseItem::product][ProductOutline::id] }
    val customers = innerJoin(customers `as` "customers") { this[Customer::id] `=` purchases[Purchase::customerId] }
    columns(
        customers[Customer::country][Country::code] `as` Customer::country.name,
        Sum.Companion(purchaseItems[PurchaseItem::amount]) `as` "_totalAmount",
    )
    where(products[Product::category][Category::name] `=` Constant("Food"))
    groupBy(customers[Customer::country][Country::code])
}
