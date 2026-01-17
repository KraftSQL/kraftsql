package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.DataRow

class SystemRange(
    val from: Expression<H2Engine, Int>,
    val to: Expression<H2Engine, Int>,
) : Data<DataRow> {

    override val columnNames = listOf("X")

    override fun sql() = "SYSTEM_RANGE(${from.sql()},${to.sql()})"
}
