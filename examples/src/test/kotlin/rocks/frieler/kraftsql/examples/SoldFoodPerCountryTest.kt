package rocks.frieler.kraftsql.examples

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.Sale
import rocks.frieler.kraftsql.examples.data.Shop
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.testing.h2.WithH2Simulator
import rocks.frieler.kraftsql.testing.matchers.collections.shouldContainExactlyOne
import java.time.Instant

@WithH2Simulator
class SoldFoodPerCountryTest {
    private val food = Category(1, "Food")
    private val clothes = Category(2, "Clothes")

    private val chocolate = Product(1, "Chocolate", food)
    private val pants = Product(2, "Pants", clothes)

    private val shop1 = Shop(1, "DE")
    private val shop2 = Shop(2, "NL")

    @Test
    fun `calculateSoldFoodPerCountry() sums up sold amounts`() {
        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(shop1),
            ConstantData(
                Sale(chocolate, shop1, Instant.EPOCH, 1),
                Sale(chocolate, shop1, Instant.EPOCH, 2),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Shop::country.name] == shop1.country }
        soldFood["_totalAmount"] shouldBe 3L
    }

    @Test
    fun `calculateSoldFoodPerCountry() counts only Food`() {
        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate, pants),
            ConstantData(shop1),
            ConstantData(
                Sale(chocolate, shop1, Instant.EPOCH, 1),
                Sale(pants, shop1, Instant.EPOCH, 1),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Shop::country.name] == shop1.country }
        soldFood["_totalAmount"] shouldBe 1L
    }

    @Test
    fun `calculateSoldFoodPerCountry() sums up per country`() {
        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(shop1, shop2),
            ConstantData(
                Sale(chocolate, shop1, Instant.EPOCH, 1),
                Sale(chocolate, shop2, Instant.EPOCH, 2),
            ),
        ).execute()

        soldFoodPerCountry shouldContainExactlyOne { it[Shop::country.name] == shop1.country }
        soldFoodPerCountry shouldContainExactlyOne { it[Shop::country.name] == shop2.country }
    }

    @Test
    fun `calculateSoldFoodPerCountry() ignores unknown products`() {
        val apple = Product(666, "Apple", food)

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(shop1),
            ConstantData(
                Sale(chocolate, shop1, Instant.EPOCH, 1),
                Sale(apple, shop1, Instant.EPOCH, 1),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Shop::country.name] == shop1.country }
        soldFood["_totalAmount"] shouldBe 1L
    }

    @Test
    fun `calculateSoldFoodPerCountry() ignores unknown shops`() {
        val unknownShop = Shop(666, "DE")

        val soldFoodPerCountry = calculateSoldFoodPerCountry(
            ConstantData(chocolate),
            ConstantData(shop1),
            ConstantData(
                Sale(chocolate, shop1, Instant.EPOCH, 1),
                Sale(chocolate, unknownShop, Instant.EPOCH, 1),
            ),
        ).execute()

        val soldFood = soldFoodPerCountry shouldContainExactlyOne { it[Shop::country.name] == shop1.country }
        soldFood["_totalAmount"] shouldBe 1L
    }
}
