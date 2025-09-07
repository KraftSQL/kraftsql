package rocks.frieler.kraftsql.examples

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.testing.WithH2Simulator
import rocks.frieler.kraftsql.testing.matchers.collections.shouldContainExactlyOne
import rocks.frieler.kraftsql.testing.matchers.collections.shouldContainNone

@WithH2Simulator
class DeleteFoodTest {
    @Test
    fun `deleteFood() deletes products from category Food`() {
        products.create()
        Product(1, "Apple", Category(1, "Food")).also { it.insertInto(products) }
        val pants = Product(2, "Pants", Category(2, "Clothes"), arrayOf("foo")).also { it.insertInto(products) }

        val deletedProductCount = deleteFood(products)

        deletedProductCount shouldBe 1
        Select<Product> { from(products) }.execute().also { remainingProducts ->
            remainingProducts shouldContainNone  { it.category.name == "Food" }
            remainingProducts shouldContainExactlyOne { it == pants }
        }
    }
}
