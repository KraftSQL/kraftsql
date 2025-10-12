package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.testing.engine.GenericSimulatorConnection

class H2SimulatorConnection : GenericSimulatorConnection<H2Engine>(orm = H2SimulatorORMapping) {
    init {
        unregisterExpressionSimulator(rocks.frieler.kraftsql.expressions.Constant::class)
        registerExpressionSimulator(ConstantSimulator())
        unregisterExpressionSimulator(rocks.frieler.kraftsql.expressions.Column::class)
        registerExpressionSimulator(ColumnSimulator<Any>())
    }
}
