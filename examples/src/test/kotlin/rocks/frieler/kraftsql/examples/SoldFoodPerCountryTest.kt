package rocks.frieler.kraftsql.examples

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Country
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.PurchaseItem
import rocks.frieler.kraftsql.examples.data.Customer
import rocks.frieler.kraftsql.examples.data.Purchase
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.testing.WithH2Simulator
import rocks.frieler.kraftsql.testing.kotest.inspectors.filterForOne
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@WithH2Simulator
class SoldFoodPerCountryTest {
    private val food = Category(1, "Food")
    private val clothes = Category(2, "Clothes")

    private val chocolate = Product(1, "Chocolate", food)
    private val pants = Product(2, "Pants", clothes)

    private val germanCustomer = Customer(1, Country("DE", "Germany"), LocalDate.EPOCH)
    private val austrianCustomer = Customer(2, Country("AT", "Austria"), LocalDate.EPOCH)

    @Test
    fun `calculateSoldFoodPerCountry() sums up sold amounts`() {
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 1), PurchaseItem(chocolate, BigDecimal.ZERO, 2)), BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer),
            ConstantData(purchase),
        ).execute()

        val soldFoodInGermany = soldFoodPerCountry.filterForOne { it[Customer::country.name] shouldBe germanCustomer.country.code }
        soldFoodInGermany["totalAmount"] shouldBe 3L
    }

    @Test
    fun `calculateSoldFoodPerCountry() counts only Food`() {
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 1), PurchaseItem(pants, BigDecimal.ZERO, 1)), BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate, pants),
            ConstantData(germanCustomer),
            ConstantData(purchase),
        ).execute()

        val soldFoodInGermany = soldFoodPerCountry.filterForOne { it[Customer::country.name] shouldBe germanCustomer.country.code }
        soldFoodInGermany["totalAmount"] shouldBe 1L
    }

    @Test
    fun `calculateSoldFoodPerCountry() sums up per country`() {
        val purchaseInGermany = Purchase(1, germanCustomer, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 1)), BigDecimal.ZERO)
        val purchaseInAustria = Purchase(2, austrianCustomer, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 2)), BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer, austrianCustomer),
            ConstantData(purchaseInGermany, purchaseInAustria),
        ).execute()

        soldFoodPerCountry.map { it[Customer::country.name] } shouldContainExactlyInAnyOrder listOf(germanCustomer.country.code, austrianCustomer.country.code)
    }

    @Test
    fun `calculateSoldFoodPerCountry() ignores unknown products`() {
        val apple = Product(666, "Apple", food)
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 1), PurchaseItem(apple, BigDecimal.ZERO, 1)),BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer),
            ConstantData(purchase),
        ).execute()

        val soldFoodInGermany = soldFoodPerCountry.filterForOne { it[Customer::country.name] shouldBe germanCustomer.country.code }
        soldFoodInGermany["totalAmount"] shouldBe 1L
    }

    @Test
    fun `calculateSoldFoodPerCountry() ignores unknown customers`() {
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 1)), BigDecimal.ZERO)
        val purchaseByUnknownCustomer = Purchase(2, 666, Instant.EPOCH, arrayOf(PurchaseItem(chocolate, BigDecimal.ZERO, 1)), BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer),
            ConstantData(purchase, purchaseByUnknownCustomer),
        ).execute()

        val soldFood = soldFoodPerCountry.filterForOne { it[Customer::country.name] shouldBe germanCustomer.country.code }
        soldFood["totalAmount"] shouldBe 1L
    }
}
