package rocks.frieler.kraftsql.h2.util

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.expressions.ArrayElementReference.Companion.get
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.expressions.lessOrEqual
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.Types
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.h2.objects.collect
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.collections.ifEmpty
import kotlin.collections.single

fun Data<*>.unnest(arrayColumn: String, elementColumn: String) : Data<DataRow> {
    // TODO: allow single-valued Data as Expression instead of .collect() and Constant(...)
    val maxElements = Select<DataRow> {
        from(this@unnest)
        groupBy(Constant(1)) // TODO: allow aggregation over all rows without group-by. Important: NULL over no rows!!!
        column(Max(ArrayLength(Column(arrayColumn))) `as` "_max_elements")
    }.collect().ifEmpty { listOf(DataRow("_max_elements" to 0)) }.single()["_max_elements"] as Int

    val unnestedData = Select<DataRow> {
        from(this@unnest)
        val indizes = innerJoin(
            Select<DataRow> { from(SystemRange(Constant(1L), Constant(maxElements.toLong()))) }) {
            this["X"] lessOrEqual ArrayLength(Column(arrayColumn))
        }
        for (column in this@unnest.columnNames) {
            column(Column<Any?>(column))
        }
        column(Column<Array<String>>(arrayColumn)[Cast(indizes["X"].knownNotNull(), Types.INTEGER)] `as` elementColumn)
    }

    return unnestedData
}
