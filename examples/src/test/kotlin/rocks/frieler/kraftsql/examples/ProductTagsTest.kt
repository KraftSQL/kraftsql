package rocks.frieler.kraftsql.examples

import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKeys
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.testing.h2.WithH2Simulator

@WithH2Simulator
class ProductTagsTest {
    private val food = Category(1, "Food")

    @Test
    fun `countTags() can handle empty data`() {
        val products = ConstantData(emptyList<Product>())

        val tagCounts = countTags(products)

        tagCounts.shouldBeEmpty()
    }

    @Test
    fun `countTags() collects all tags`() {
        val products = ConstantData(
            Product(1, "Chocolate", food, tags = arrayOf("sweets")),
            Product(2, "Lemon", food, tags = arrayOf("sour")),
        )

        val tagCounts = countTags(products)

        tagCounts.shouldContainKeys("sweets", "sour")
    }

    @Test
    fun `countTags() counts occurrences per tag`() {
        val products = ConstantData(
            Product(1, "Banana", food, tags = arrayOf("fruit", "sweet")),
            Product(2, "Lemon", food, tags = arrayOf("fruit", "sour")),
        )

        val tagCounts = countTags(products)

        tagCounts.shouldContain("fruit", 2L)
        tagCounts.shouldContain("sweet", 1L)
        tagCounts.shouldContain("sour", 1L)
    }
}
