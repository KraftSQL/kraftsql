package rocks.frieler.kraftsql.examples

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
import rocks.frieler.kraftsql.testing.matchers.collections.shouldContainExactlyOne
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
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, BigDecimal.ZERO)
        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer),
            ConstantData(purchase),
            ConstantData(
                PurchaseItem(purchase, chocolate, BigDecimal.ZERO, 1),
                PurchaseItem(purchase, chocolate, BigDecimal.ZERO, 2),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Customer::country.name] == germanCustomer.country.code }
        soldFood["_totalAmount"] shouldBe 3L
    }

    @Test
    fun `calculateSoldFoodPerCountry() counts only Food`() {
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, BigDecimal.ZERO)
        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate, pants),
            ConstantData(germanCustomer),
            ConstantData(purchase),
            ConstantData(
                PurchaseItem(purchase, chocolate, BigDecimal.ZERO, 1),
                PurchaseItem(purchase, pants, BigDecimal.ZERO, 1),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Customer::country.name] == germanCustomer.country.code }
        soldFood["_totalAmount"] shouldBe 1L
    }

    @Test
    fun `calculateSoldFoodPerCountry() sums up per country`() {
        val purchaseInGermany = Purchase(1, germanCustomer, Instant.EPOCH, BigDecimal.ZERO)
        val purchaseInAustria = Purchase(2, austrianCustomer, Instant.EPOCH, BigDecimal.ZERO)
        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer, austrianCustomer),
            ConstantData(purchaseInGermany, purchaseInAustria),
            ConstantData(
                PurchaseItem(purchaseInGermany, chocolate, BigDecimal.ZERO, 1),
                PurchaseItem(purchaseInAustria, chocolate, BigDecimal.ZERO, 2),
            ),
        ).execute()

        soldFoodPerCountry shouldContainExactlyOne { it[Customer::country.name] == germanCustomer.country.code }
        soldFoodPerCountry shouldContainExactlyOne { it[Customer::country.name] == austrianCustomer.country.code }
    }

    @Test
    fun `calculateSoldFoodPerCountry() ignores unknown products`() {
        val apple = Product(666, "Apple", food)
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer),
            ConstantData(purchase),
            ConstantData(
                PurchaseItem(purchase, chocolate, BigDecimal.ZERO, 1),
                PurchaseItem(purchase, apple, BigDecimal.ZERO, 1),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Customer::country.name] == germanCustomer.country.code }
        soldFood["_totalAmount"] shouldBe 1L
    }

    @Test
    fun `calculateSoldFoodPerCountry() ignores unknown customers`() {
        val purchase = Purchase(1, germanCustomer, Instant.EPOCH, BigDecimal.ZERO)
        val purchaseByUnknownCustomer = Purchase(2, 666, Instant.EPOCH, BigDecimal.ZERO)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(germanCustomer),
            ConstantData(purchase, purchaseByUnknownCustomer),
            ConstantData(
                PurchaseItem(purchase, chocolate, BigDecimal.ZERO, 1),
                PurchaseItem(purchaseByUnknownCustomer, chocolate, BigDecimal.ZERO, 1),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Customer::country.name] == germanCustomer.country.code }
        soldFood["_totalAmount"] shouldBe 1L
    }
}
