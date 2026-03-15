package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.engine.ArrayConcatenationSimulator
import rocks.frieler.kraftsql.testing.engine.GenericQueryEvaluator
import rocks.frieler.kraftsql.testing.engine.GenericSimulatorConnection
import rocks.frieler.kraftsql.testing.engine.GenericEngineSimulator
import kotlin.reflect.KClass

class H2SimulatorConnection : GenericSimulatorConnection<H2Engine>(
    orm = H2SimulatorORMapping,
    engine = GenericEngineSimulator(
        expressionEvaluator = H2ExpressionEvaluator,
        queryEvaluator = H2QueryEvaluator,
    ),
)
