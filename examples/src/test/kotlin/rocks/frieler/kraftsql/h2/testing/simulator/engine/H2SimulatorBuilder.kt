package rocks.frieler.kraftsql.h2.testing.simulator.engine

import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.h2.testing.simulator.expressions.ColumnSimulator
import rocks.frieler.kraftsql.h2.testing.simulator.expressions.ConstantSimulator
import rocks.frieler.kraftsql.h2.testing.simulator.expressions.RowSimulator
import rocks.frieler.kraftsql.testing.simulator.engine.GenericEngineSimulator
import rocks.frieler.kraftsql.testing.simulator.engine.GenericEngineSimulatorBuilderTemplate
import rocks.frieler.kraftsql.testing.simulator.expressions.ArrayConcatenationSimulator
import kotlin.reflect.KClass

class H2SimulatorBuilder : GenericEngineSimulatorBuilderTemplate<H2Engine, GenericEngineSimulator<H2Engine>>() {
    override val orm = H2SimulatorORMapping
    override val queryEvaluator = H2QueryEvaluator(orm, expressionEvaluator)

    override fun registerExpressionSimulators() {
        super.registerExpressionSimulators()

        expressionEvaluator.apply {
            unregisterExpressionSimulator(Constant::class)
            registerExpressionSimulator(ConstantSimulator<Any?>())
            unregisterExpressionSimulator(Column::class)
            registerExpressionSimulator(ColumnSimulator<Any?>())
            unregisterExpressionSimulator(Row::class)
            registerExpressionSimulator(RowSimulator())

            @Suppress("UNCHECKED_CAST")
            registerExpressionSimulator(ArrayConcatenationSimulator(ArrayConcatenation::class as KClass<ArrayConcatenation<Any?>>))
        }
    }

    override fun makeSimulator() = GenericEngineSimulator(orm, persistentState, expressionEvaluator, queryEvaluator)
}
