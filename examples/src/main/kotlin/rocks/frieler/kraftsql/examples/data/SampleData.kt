package rocks.frieler.kraftsql.examples.data

import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.ddl.drop
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.objects.Table
import java.time.Instant
import java.time.LocalDate

fun with(vararg tables: Table<*>, action: () -> Unit) {
    try {
        tables.forEach { it.create() }
        action()
    } finally {
        tables.reversed().forEach { it.drop(ifExists = true) }
    }
}

fun withSampleData(action: () -> Unit) {
    with(products, customers, purchases) {
        val food = Category(1, "Food")
        val clothes = Category(2, "Clothes")

        val chocolate = Product(1, "Chocolate", food, arrayOf("sweets", "snacks")).also { it.insertInto(products) }
        val banana = Product(2, "Banana", food, arrayOf("fruit")).also { it.insertInto(products) }
        val pants = Product(3, "Pants", clothes, arrayOf("jeans, blue")).also { it.insertInto(products) }

        val germany = Country("DE", "Deutschland")
        val austria = Country("AT", "Österreich")
        val customer1 = Customer(1, germany, LocalDate.of(1987, 5, 17)).also { it.insertInto(customers) }
        val customer2 = Customer(2, austria, LocalDate.of(1995, 3, 4)).also { it.insertInto(customers) }
        val customer3 = Customer(3, germany, LocalDate.of(2001, 11, 30)).also { it.insertInto(customers) }
        @Suppress("UnusedVariable", "unused") // this customer has not bought anything yet
        val customer4 = Customer(4, germany, LocalDate.of(2006, 8, 3)).also { it.insertInto(customers) }

        Purchase(
            id = 1,
            customer = customer1,
            orderTime = Instant.parse("2025-01-03T08:22:14+01:00"),
            items = arrayOf(
                PurchaseItem(chocolate, 1.95.toBigDecimal(), 2),
                PurchaseItem(pants, 39.90.toBigDecimal(), 1),
            ),
            totalPrice = 43.80.toBigDecimal(),
        ).also { it.insertInto(purchases) }

        Purchase(
            id = 2,
            customer = customer2,
            orderTime = Instant.parse("2025-01-03T09:04:37+01:00"),
            items = arrayOf(
                PurchaseItem(chocolate, 1.95.toBigDecimal(), 1),
                PurchaseItem(banana, 0.90.toBigDecimal(), 3),
            ),
            totalPrice = 4.65.toBigDecimal(),
        ).also { it.insertInto(purchases) }

        Purchase(
            id = 3,
            customer = customer3,
            orderTime = Instant.parse("2025-01-03T14:25:02+01:00"),
            items = arrayOf(PurchaseItem(pants, 39.90.toBigDecimal(), 1)),
            totalPrice = 39.90.toBigDecimal(),
        ).also { it.insertInto(purchases) }

        Purchase(
            id = 4,
            customer = customer1,
            orderTime = Instant.parse("2025-01-04T08:04:48+01:00"),
            items = arrayOf(PurchaseItem(banana, 0.90.toBigDecimal(), 2)),
            totalPrice = 1.80.toBigDecimal(),
        ).also { it.insertInto(purchases) }

        action()
    }
}
