package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.testing.engine.ColumnSimulator
import kotlin.reflect.KClass

class ColumnSimulator<T : Any> : ColumnSimulator<H2Engine, T>() {
    @Suppress("UNCHECKED_CAST")
    override val expression = Column::class as KClass<Column<T>>
}
