package rocks.frieler.kraftsql.h2.expressions

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.Types

open class Constant<E : Engine<E>, T>(
    value: T,
) : rocks.frieler.kraftsql.expressions.Constant<H2Engine, T>(value) {
    override fun sql(): String {
        return when (value) {
            is Long -> "CAST(${value} AS ${Types.BIGINT.sql()})"
            else -> super.sql()
        }
    }
}
