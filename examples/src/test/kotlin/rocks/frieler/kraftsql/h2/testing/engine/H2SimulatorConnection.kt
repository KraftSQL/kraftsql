package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.testing.engine.ArrayConcatenationSimulator
import rocks.frieler.kraftsql.testing.engine.GenericSimulatorConnection
import kotlin.reflect.KClass

class H2SimulatorConnection : GenericSimulatorConnection<H2Engine>(orm = H2SimulatorORMapping) {
    init {
        unregisterExpressionSimulator(rocks.frieler.kraftsql.expressions.Constant::class)
        registerExpressionSimulator(ConstantSimulator<Any?>())
        unregisterExpressionSimulator(rocks.frieler.kraftsql.expressions.Column::class)
        registerExpressionSimulator(ColumnSimulator<Any?>())
        unregisterExpressionSimulator(rocks.frieler.kraftsql.expressions.Row::class)
        registerExpressionSimulator(RowSimulator())

        @Suppress("UNCHECKED_CAST")
        registerExpressionSimulator(ArrayConcatenationSimulator(ArrayConcatenation::class as KClass<ArrayConcatenation<Any?>>))
    }
}
