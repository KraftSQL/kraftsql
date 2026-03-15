package rocks.frieler.kraftsql.h2.testing.simulator.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.testing.simulator.expressions.H2ExpressionEvaluator
import rocks.frieler.kraftsql.testing.simulator.engine.GenericSimulatorConnection
import rocks.frieler.kraftsql.testing.simulator.engine.GenericEngineSimulator

class H2SimulatorConnection : GenericSimulatorConnection<H2Engine>(
    orm = H2SimulatorORMapping,
    engine = GenericEngineSimulator(
        expressionEvaluator = H2ExpressionEvaluator,
        queryEvaluator = H2QueryEvaluator,
    ),
)
