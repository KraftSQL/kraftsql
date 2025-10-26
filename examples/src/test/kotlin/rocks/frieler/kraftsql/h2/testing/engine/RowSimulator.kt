package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.Row
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.engine.RowSimulator
import kotlin.reflect.KClass

class RowSimulator : RowSimulator<H2Engine>() {
    @Suppress("UNCHECKED_CAST")
    override val expression = Row::class as KClass<out Row<DataRow?>>
}
