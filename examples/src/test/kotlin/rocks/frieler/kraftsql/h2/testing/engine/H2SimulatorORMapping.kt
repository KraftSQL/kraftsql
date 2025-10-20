package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2ORMapping
import rocks.frieler.kraftsql.testing.engine.SimulatorORMapping

object H2SimulatorORMapping : SimulatorORMapping<H2Engine>() {
    override fun <T : Any> serialize(value: T?) = H2ORMapping.serialize(value)
}
