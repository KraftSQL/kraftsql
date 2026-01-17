package rocks.frieler.kraftsql.examples

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.examples.data.Product
import rocks.frieler.kraftsql.examples.data.products
import rocks.frieler.kraftsql.examples.data.withSampleData
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.Types
import rocks.frieler.kraftsql.h2.expressions.ArrayElementReference
import rocks.frieler.kraftsql.h2.expressions.ArrayLength
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.LessOrEqual
import rocks.frieler.kraftsql.h2.expressions.Max
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.collect
import rocks.frieler.kraftsql.objects.DataRow

fun main() {
    withSampleData {
        // TODO: allow single-valued Data as Expression instead of .collect() and Constant(...)
        val maxProductTags = Select<DataRow> {
            from(products)
            column(Max(ArrayLength(products[Product::tags])) `as` "max")
        }.collect().single()["max"] as Int

        Select<DataRow> {
            from(products)
            val indizes = innerJoin(Select<DataRow> { from(SystemRange(Constant(1), Constant(maxProductTags))) }) { LessOrEqual(this["X"], ArrayLength(products[Product::tags])) }
            column(products[Product::name])
            column(ArrayElementReference(products[Product::tags], Cast(indizes["X"], Types.INTEGER).knownNotNull()) `as` "tag")
        }
            .collect()
            .forEach { println(it) }
    }
}
