package rocks.frieler.kraftsql.expressions

import rocks.frieler.kraftsql.engine.Engine
import kotlin.reflect.KProperty1

class Column<E: Engine<E>, T>(
    private val name: String,
) : Expression<E, T> {
    override fun sql() = "`$name`"

    companion object {
        fun <E : Engine<E>, T> forProperty(property: KProperty1<*, T>) = Column<E, T>(property.name)
    }
}
