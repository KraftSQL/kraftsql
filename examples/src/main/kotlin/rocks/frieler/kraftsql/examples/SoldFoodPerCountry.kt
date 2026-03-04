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
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Country
import rocks.frieler.kraftsql.examples.data.ProductOutline
import rocks.frieler.kraftsql.examples.data.Purchase
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.expressions.ArrayElementReference.Companion.get
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.LessOrEqual
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.expressions.lessOrEqual
import rocks.frieler.kraftsql.h2.dql.Select
import rocks.frieler.kraftsql.h2.dsl.`as`
import rocks.frieler.kraftsql.h2.engine.Types
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.collect
import kotlin.collections.ifEmpty
import kotlin.collections.single

fun main() {
    withSampleData {
        calculateSoldFoodPerCountry(products, customers, purchases)
            .execute().forEach {
                println("${it[Customer::country.name]}: ${it["totalAmount"]}")
            }
    }
}

fun calculateSoldFoodPerCountry(products: Data<Product>, customers: Data<Customer>, purchases: Data<Purchase>): Select<DataRow> {
    // TODO: allow single-valued Data as Expression instead of .collect() and Constant(...)
    val maxItems = Select<DataRow> {
        from(purchases)
        groupBy(Constant(1)) // TODO: allow aggregation over all rows without group-by. Important: NULL over no rows!!!
        column(Max(ArrayLength(purchases[Purchase::items])) `as` "max")
    }.collect().ifEmpty { listOf(DataRow("max" to 0)) }.single()["max"] as Int

    @Suppress("PropertyName")
    data class PurchaseItemOfCustomer(
        val _item: PurchaseItem,
        val _customerId: Long,
    )

    val purchaseItemsOfCustomers = Select<PurchaseItemOfCustomer> {
        from(purchases)
        val indizes = innerJoin(
            Select<DataRow> { from(SystemRange(Constant(1L), Constant(maxItems.toLong()))) }) {
            this["X"] lessOrEqual ArrayLength(purchases[Purchase::items])
        }
        column(purchases[Purchase::items][Cast(indizes["X"].knownNotNull(), Types.INTEGER)] `as` PurchaseItemOfCustomer::_item)
        column(purchases[Purchase::customerId] `as` PurchaseItemOfCustomer::_customerId)
    }

    return Select {
        from(purchaseItemsOfCustomers)
        val products = innerJoin(products `as` "products") {
            this[Product::id] `=` purchaseItemsOfCustomers[PurchaseItemOfCustomer::_item][PurchaseItem::product][ProductOutline::id]
        }
        val customers = innerJoin(customers `as` "customers") {
            this[Customer::id] `=` purchaseItemsOfCustomers[PurchaseItemOfCustomer::_customerId]
        }
        columns(
            customers[Customer::country][Country::code] `as` Customer::country.name,
            Sum(purchaseItemsOfCustomers[PurchaseItemOfCustomer::_item][PurchaseItem::amount]) `as` "totalAmount",
        )
        where(products[Product::category][Category::name] `=` Constant("Food"))
        groupBy(customers[Customer::country][Country::code])
    }
}
