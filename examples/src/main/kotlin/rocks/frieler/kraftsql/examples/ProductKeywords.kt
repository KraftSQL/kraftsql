package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.ddl.drop
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.objects.ConstantData
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.h2.objects.collect
import rocks.frieler.kraftsql.objects.DataRow

fun main() {
    try {
        val food = Category(1, "Food")
        val clothes = Category(2, "Clothes")
        val other = Category(3, "Other")

        products.create()
        Product(1, "Chocolate", food, tags = arrayOf("sweets", "snacks")).also { it.insertInto(products) }
        Product(2, "Pants", clothes, tags = arrayOf()).also { it.insertInto(products) }
        Product(3, "Crisps", food, tags = arrayOf("snacks")).also { it.insertInto(products) }
        Product(4, "Crap", other, tags = arrayOf("bullshit")).also { it.insertInto(products) }

        val productKeywords = collectProductKeywords(products)
        val wordCounts = countKeywords(productKeywords)
        wordCounts
            .forEach { (keywords, count) -> println("$keywords: $count") }

    } finally {
        products.drop(ifExists = true)
    }
}

fun collectProductKeywords(products: Data<Product>) : Data<DataRow> =
    Select<DataRow> {
        from(products)
        columns(
            rocks.frieler.kraftsql.expressions.Array(
                products[Product::name],
                products[Product::category][Category::name]
            ) `as` "part1",
            products[Product::tags] `as` "part2",
        )
    }
        .execute()
        .map {
            // TODO: Concat arrays in SQL, once this is implemented.
            @Suppress("UNCHECKED_CAST")
            DataRow("keywords" to (it["part1"] as Array<String> + it["part2"] as Array<String>))
        }
        .let { result -> if (result.isNotEmpty()) ConstantData(result) else ConstantData.empty(listOf("keywords")) }

fun countKeywords(words: Data<DataRow>): Map<String, Long> {
    val wordCounts = words.collect()
        .map { row ->
            @Suppress("UNCHECKED_CAST")
            row["keywords"] as Array<String>
        }
        .fold(mutableMapOf<String, Long>()) { stats, tags ->
            tags.forEach { tag ->
                stats.compute(tag) { _, count -> count?.plus(1) ?: 1 }
            }
            stats
        }
    return wordCounts
}
