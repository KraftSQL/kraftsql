package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.h2.objects.collect
import rocks.frieler.kraftsql.h2.util.unnest
import rocks.frieler.kraftsql.objects.DataRow

fun main() {
    withSampleData {
        val keywordCounts = collectProductKeywords(products)
        keywordCounts.collect()
            .forEach { println(it) }
    }
}

fun collectProductKeywords(products: Data<Product>) : Data<DataRow> {
    val keywordArrays = Select<DataRow> {
        from(products)
        column(ArrayConcatenation(Array(products[Product::name], products[Product::category][Category::name]), products[Product::tags]) `as` "_keywords")
    }
    val keywords = keywordArrays.unnest("_keywords", "_keyword")
    return Select {
        from(keywords)
        groupBy(Column<String>("_keyword"))
        column(Column<String>("_keyword") `as` "keyword")
        column(Count<H2Engine>() `as` "count")
    }
}
