package rocks.frieler.kraftsql.h2.testing.simulator.expressions

import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.testing.simulator.expressions.ColumnSimulator
import kotlin.reflect.KClass

class ColumnSimulator<T> : ColumnSimulator<H2Engine, T>() {
    @Suppress("UNCHECKED_CAST")
    override val expression = Column::class as KClass<Column<T>>
}
