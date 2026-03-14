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
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.h2.dql.Select
import rocks.frieler.kraftsql.h2.dsl.`as`
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.util.unnest

fun main() {
    withSampleData {
        calculateSoldFoodPerCountry(products, customers, purchases)
            .execute().forEach {
                println("${it[Customer::country.name]}: ${it["totalAmount"]}")
            }
    }
}

fun calculateSoldFoodPerCountry(products: Data<Product>, customers: Data<Customer>, purchases: Data<Purchase>): Select<DataRow> {
    @Suppress("PropertyName")
    data class PurchaseItemOfCustomer(
        val _item: PurchaseItem,
        val _customerId: Long,
    )

    val purchaseItemsOfCustomers = Select<PurchaseItemOfCustomer> {
        val unnestedPurchases = from(purchases.unnest(Purchase::items.name, PurchaseItemOfCustomer::_item.name))
        column(unnestedPurchases[PurchaseItemOfCustomer::_item.name].knownNotNull() `as` PurchaseItemOfCustomer::_item)
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
