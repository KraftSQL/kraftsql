package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.ddl.drop
import rocks.frieler.kraftsql.h2.dml.insertInto
import java.time.Instant

fun withSampleData(action: () -> Unit) {
    try {
        val food = Category(1, "Food")
        val clothes = Category(2, "Clothes")

        products.create()
        val chocolate = Product(1, "Chocolate", food).also { it.insertInto(products) }
        val banana = Product(2, "Banana", food).also { it.insertInto(products) }
        val pants = Product(3, "Pants", clothes).also { it.insertInto(products) }

        customers.create()
        val germany = Country("DE", "Deutschland")
        val austria = Country("AT", "Österreich")
        val customer1 = Customer(1, germany).also { it.insertInto(customers) }
        val customer2 = Customer(2, austria).also { it.insertInto(customers) }
        val customer3 = Customer(3, germany).also { it.insertInto(customers) }

        purchases.create()
        purchaseItems.create()
        Purchase(1, customer1, Instant.parse("2025-01-03T08:22:14+01:00"), 43.80.toBigDecimal()).also {
            it.insertInto(purchases)
            PurchaseItem(it, chocolate, 1.95.toBigDecimal(), 2).insertInto(purchaseItems)
            PurchaseItem(it, pants, 39.90.toBigDecimal(), 1).insertInto(purchaseItems)
        }
        Purchase(2, customer2, Instant.parse("2025-01-03T09:04:37+01:00"), 4.65.toBigDecimal()).also {
            it.insertInto(purchases)
            PurchaseItem(it, chocolate, 1.95.toBigDecimal(), 1).insertInto(purchaseItems)
            PurchaseItem(it, banana, 0.90.toBigDecimal(), 3).insertInto(purchaseItems)
        }
        Purchase(3, customer3, Instant.parse("2025-01-03T14:25:02+01:00"), 39.90.toBigDecimal()).also {
            it.insertInto(purchases)
            PurchaseItem(it, pants, 39.90.toBigDecimal(), 1).insertInto(purchaseItems)
        }
        Purchase(4, customer1, Instant.parse("2025-01-04T08:04:48+01:00"), 1.80.toBigDecimal()).also {
            it.insertInto(purchases)
            PurchaseItem(it, banana, 0.90.toBigDecimal(), 2).insertInto(purchaseItems)
        }

        action()

    } finally {
        purchaseItems.drop(ifExists = true)
        purchases.drop(ifExists = true)
        customers.drop(ifExists = true)
        products.drop(ifExists = true)
    }
}
