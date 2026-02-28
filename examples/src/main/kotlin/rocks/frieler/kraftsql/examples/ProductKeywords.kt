package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Category
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.ArrayElementReference.Companion.get
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.LessOrEqual
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.Types
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.h2.objects.collect
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

    // TODO: allow single-valued Data as Expression instead of .collect() and Constant(...)
    val maxKeywords = Select<DataRow> {
        from(keywordArrays)
        groupBy(Constant(1)) // TODO: allow aggregation over all rows without group-by. Important: NULL over no rows!!!
        column(Max(ArrayLength(Column("_keywords"))) `as` "max")
    }.collect().ifEmpty { listOf(DataRow("max" to 0)) }.single()["max"] as Int

    val keywords = Select<DataRow> {
        from(keywordArrays)
        val indizes = innerJoin(
            Select<DataRow> { from(SystemRange(Constant(1L), Constant(maxKeywords.toLong()))) }) {
            LessOrEqual(this["X"], ArrayLength(Column("_keywords")))
        }
        column(Column<kotlin.Array<String>>("_keywords")[Cast(indizes["X"], Types.INTEGER).knownNotNull()] `as` "_keyword")
    }

    return Select {
        from(keywords)
        groupBy(Column<String>("_keyword"))
        column(Column<String>("_keyword") `as` "keyword")
        column(Count<H2Engine>() `as` "count")
    }
}
