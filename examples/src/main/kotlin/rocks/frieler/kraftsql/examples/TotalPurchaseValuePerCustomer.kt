package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Customer
import rocks.frieler.kraftsql.examples.data.Purchase
import rocks.frieler.kraftsql.examples.data.customers
import rocks.frieler.kraftsql.examples.data.purchases
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.dsl.`as`
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Data
import java.math.BigDecimal

fun main() {
    withSampleData {
        aggregatePurchaseValuePerCustomer(customers, purchases)
            .execute()
            .forEach { println(it) }
    }
}

data class CustomerPurchaseValue(val customerId: Long, val totalAmount: BigDecimal)

fun aggregatePurchaseValuePerCustomer(customers: Data<H2Engine, Customer>, purchases: Data<H2Engine, Purchase>) =
    Select<CustomerPurchaseValue> {
        from(purchases)
        val customers = innerJoin(customers `as` "customers") { this[Customer::id] `=` purchases[Purchase::customerId] }
        columns(
            customers[Customer::id] `as` CustomerPurchaseValue::customerId.name,
            Sum.Companion(purchases[Purchase::totalPrice]) `as` CustomerPurchaseValue::totalAmount.name,
        )
        groupBy(customers[Customer::id])
    }
