package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.h2.ddl.create
import rocks.frieler.kraftsql.h2.ddl.drop
import rocks.frieler.kraftsql.h2.dml.insertInto
import rocks.frieler.kraftsql.h2.dql.execute
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.Row

fun main() {
    try {
        products.create()
        Product(1, "Chocolate", "Food", tags = arrayOf("sweets", "snacks")).also { it.insertInto(products) }
        Product(2, "Pants", "Clothes", tags = arrayOf()).also { it.insertInto(products) }
        Product(3, "Crisps", "Food", tags = arrayOf("snacks")).also { it.insertInto(products) }
        Product(4, "Crap", "Other", tags = arrayOf("bullshit")).also { it.insertInto(products) }

        val productsOfInterest = Select<Product> { from(products) }
        val tagCounts = countTags(productsOfInterest)
        tagCounts
            .forEach { (tag, count) -> println("$tag: $count") }

    } finally {
        products.drop(ifExists = true)
    }
}

fun countTags(productsOfInterest: Data<H2Engine, Product>): MutableMap<String, Long> {
    val tagCounts = Select<Row> {
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
