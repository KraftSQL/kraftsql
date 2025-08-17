package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.ddl.drop
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Data
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

        val productsOfInterest = Select<Product> { from(products) }
        val tagCounts = countTags(productsOfInterest)
        tagCounts
            .forEach { (tag, count) -> println("$tag: $count") }

    } finally {
        products.drop(ifExists = true)
    }
}

fun countTags(productsOfInterest: Data<H2Engine, Product>): Map<String, Long> {
    val tagCounts = Select<DataRow> {
        from(productsOfInterest)
        columns(Projection(productsOfInterest[Product::tags]))
    }.execute()
        .map { row ->
            @Suppress("UNCHECKED_CAST")
            row[Product::tags.name] as Array<*>
        }
        .fold(mutableMapOf<String, Long>()) { stats, tags ->
            tags.forEach { tag ->
                stats.compute(tag as String) { _, count -> count?.plus(1) ?: 1 }
            }
            stats
        }
    return tagCounts
}
