package rocks.frieler.kraftsql.h2.util

import rocks.frieler.kraftsql.dsl.`as`
import rocks.frieler.kraftsql.expressions.ArrayElementReference.Companion.get
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.asExpression
import rocks.frieler.kraftsql.expressions.knownNotNull
import rocks.frieler.kraftsql.expressions.lessOrEqual
import rocks.frieler.kraftsql.h2.dsl.Select
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.Types
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.DataRow

fun Data<*>.unnest(arrayColumn: String, elementColumn: String) : Data<DataRow> {
    val maxElements = Select<DataRow> {
        from(this@unnest)
        column(Coalesce(Max(ArrayLength(Column(arrayColumn))), Constant(0)))
    }

    val unnestedData = Select<DataRow> {
        from(this@unnest)
        val indizes = innerJoin(
            Select<DataRow> { from(SystemRange(Constant(1L), maxElements.asExpression<H2Engine, Long>().knownNotNull())) }) {
            this["X"] lessOrEqual ArrayLength(Column(arrayColumn))
        }
        for (column in this@unnest.selectableColumnNames) {
            column(Column<Any?>(column))
        }
        column(Column<Array<String>>(arrayColumn)[Cast(indizes["X"].knownNotNull(), Types.INTEGER)] `as` elementColumn)
    }

    return unnestedData
}
