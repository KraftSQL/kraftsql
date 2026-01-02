package rocks.frieler.kraftsql.examples

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKeys
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.h2.dql.QuerySource
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.h2.testing.WithH2Simulator

@WithH2Simulator
class ProductKeywordsTest {
    private val food = Category(1, "Food")

    @Test
    fun `collectProductKeywords() can handle empty data`() {
        val products = ConstantData.empty<Product>()

        val keywords = Select<DataRow> {
            from(QuerySource(collectProductKeywords(products)))
        }.execute()

        keywords.shouldBeEmpty()
    }

    @Test
    fun `collectProductKeywords() collects name, category and tags per Product`() {
        val products = ConstantData(
            Product(1, "Chocolate", food, tags = arrayOf("sweets")),
            Product(2, "Lemon", food, tags = arrayOf("sour")),
        )

        val keywords = Select<DataRow> {
            from(QuerySource(collectProductKeywords(products)))
        }.execute()

        keywords.shouldContainAll(
            DataRow("keywords" to arrayOf("Chocolate", "Food", "sweets")),
            DataRow("keywords" to arrayOf("Lemon", "Food", "sour")),
        )
    }

    @Test
    fun `countKeywords() can handle empty data`() {
        val words = ConstantData.empty<DataRow>(listOf("keywords"))

        val wordCounts = countKeywords(words)

        wordCounts.shouldBeEmpty()
    }

    @Test
    fun `countKeywords() collects all words`() {
        val words = ConstantData(
            DataRow("keywords" to arrayOf("sweets")),
            DataRow("keywords" to arrayOf("sour")),
        )

        val wordCounts = countKeywords(words)

        wordCounts.shouldContainKeys("sweets", "sour")
    }

    @Test
    fun `countKeywords() counts occurrences per word`() {
        val words = ConstantData(
            DataRow("keywords" to arrayOf("fruit", "sweet")),
            DataRow("keywords" to arrayOf("fruit", "sour")),
        )

        val wordCounts = countKeywords(words)

        wordCounts.shouldContain("fruit", 2L)
        wordCounts.shouldContain("sweet", 1L)
        wordCounts.shouldContain("sour", 1L)
    }
}
