package rocks.frieler.kraftsql.examples

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.objects.collect
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.h2.testing.simulator.WithH2Simulator

@WithH2Simulator
class ProductKeywordsTest {
    private val food = Category(1, "Food")

    @Test
    fun `collectProductKeywords() counts name, category and tags of Products`() {
        val products = ConstantData(
            Product(1, "Chocolate", food, tags = arrayOf("sweets")),
            Product(2, "Lemon", food, tags = arrayOf("sour")),
        )

        val keywords = collectProductKeywords(products).collect()

        keywords shouldContainExactlyInAnyOrder listOf (
            DataRow("keyword" to "Chocolate", "count" to 1L),
            DataRow("keyword" to "Food", "count" to 2L),
            DataRow("keyword" to "Lemon", "count" to 1L),
            DataRow("keyword" to "sweets", "count" to 1L),
            DataRow("keyword" to "sour", "count" to 1L),
        )
    }

    @Test
    fun `collectProductKeywords() can handle Product without tags`() {
        val products = ConstantData(
            Product(1, "Chocolate", food, tags = arrayOf("sweets")),
            Product(2, "Lemon", food),
        )

        val keywords = collectProductKeywords(products).collect()

        keywords shouldContainExactlyInAnyOrder listOf (
            DataRow("keyword" to "Chocolate", "count" to 1L),
            DataRow("keyword" to "Food", "count" to 2L),
            DataRow("keyword" to "Lemon", "count" to 1L),
            DataRow("keyword" to "sweets", "count" to 1L),
        )
    }

    @Test
    fun `collectProductKeywords() can handle empty product data`() {
        val products = ConstantData.empty<Product>()

        val keywordCounts = collectProductKeywords(products).collect()

        keywordCounts.shouldBeEmpty()
    }
}
