package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Customer
import rocks.frieler.kraftsql.examples.data.Purchase
import rocks.frieler.kraftsql.examples.data.customers
import rocks.frieler.kraftsql.examples.data.purchases
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.dsl.`as`
import rocks.frieler.kraftsql.h2.objects.Data
import java.math.BigDecimal

fun main() {
    withSampleData {
        aggregatePurchaseValuePerCustomer(customers, purchases)
            .execute()
            .forEach { println(it) }
    }
}

data class CustomerPurchaseValue(val customerId: Long, val totalAmount: BigDecimal)

fun aggregatePurchaseValuePerCustomer(customers: Data<Customer>, purchases: Data<Purchase>) =
    Select<CustomerPurchaseValue> {
        from(purchases)
        val customers = innerJoin(customers `as` "customers") { this[Customer::id] `=` purchases[Purchase::customerId] }
        columns(
            customers[Customer::id] `as` CustomerPurchaseValue::customerId,
            Sum(purchases[Purchase::totalPrice]).knownNotNull() `as` CustomerPurchaseValue::totalAmount,
        )
        groupBy(customers[Customer::id])
    }
