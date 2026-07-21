package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.testing.simulator.expressions.AndSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.ArrayElementReferenceSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.ArrayLengthSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.ArraySimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.CastSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.CoalesceSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.ColumnSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.ConstantSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.CountSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.EqualsSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator
import rocks.frieler.kraftsql.testing.simulator.expressions.IsNotNullSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.LessOrEqualSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.MaxSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.MinSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.NotSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.OrSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.RowSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.SubqueryExpressionSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.SumAsBigDecimalSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.SumAsDoubleSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.SumAsLongSimulator

/**
 * Template class for a builder to construct and configure a [GenericEngineSimulator].
 *
 * IMPORTANT: This builder encapsulates the wiring of all components for a [GenericEngineSimulator]. Doing so, it
 * mutates these parts, especially the [GenericExpressionEvaluator] by registering
 * [rocks.frieler.kraftsql.testing.simulator.expressions.ExpressionSimulator]s. Hence, no component nor the builder
 * itself must be reused to build more than one [GenericEngineSimulator].
 *
 * @param E the [Engine] to simulate
 * @param S the actual type of [EngineSimulator] built
 */
abstract class GenericEngineSimulatorBuilderTemplate<E : Engine<E>, S : GenericEngineSimulator<E>> {
    protected open val orm : SimulatorORMapping<E> = SimulatorORMapping()
    protected open val persistentState : EngineState<E> = EngineState()
    protected open val expressionEvaluator : GenericExpressionEvaluator<E> = GenericExpressionEvaluator()
    protected open val queryEvaluator : GenericQueryEvaluator<E> by lazy {
        GenericQueryEvaluator(expressionEvaluator = expressionEvaluator)
    }

    private var consumed = false

    /**
     * Registers all [rocks.frieler.kraftsql.testing.simulator.expressions.ExpressionSimulator]s for all expressions
     * supported by the [Engine].
     *
     * The base implementation registers all [rocks.frieler.kraftsql.testing.simulator.expressions.ExpressionSimulator]s
     * for all expressions commonly supported by most [Engine]s.
     *
     * Subclasses for specific [Engine]s can override this to customize the supported expressions. Unless they need to
     * configure all expressions from scratch, they should call the super-class implementation first and then add or
     * replace simulators for [Engine]s-specific expressions.
     */
    protected open fun registerExpressionSimulators() {
        expressionEvaluator.apply {
            registerExpressionSimulator(ConstantSimulator<E, Any?>())
            registerExpressionSimulator(ColumnSimulator<E, Any?>())
            registerExpressionSimulator(CastSimulator<E, Any?>())
            registerExpressionSimulator(IsNotNullSimulator())
            registerExpressionSimulator(EqualsSimulator())
            registerExpressionSimulator(LessOrEqualSimulator())
            registerExpressionSimulator(NotSimulator())
            registerExpressionSimulator(AndSimulator())
            registerExpressionSimulator(OrSimulator())
            registerExpressionSimulator(CoalesceSimulator<E, Any?>())
            registerExpressionSimulator(ArraySimulator<E, Any>())
            registerExpressionSimulator(ArrayElementReferenceSimulator<E, Any?>())
            registerExpressionSimulator(ArrayLengthSimulator())
            registerExpressionSimulator(RowSimulator())
            registerExpressionSimulator(CountSimulator())
            registerExpressionSimulator(MinSimulator<E, Comparable<Comparable<*>>>())
            registerExpressionSimulator(MaxSimulator<E, Comparable<Comparable<*>>>())
            registerExpressionSimulator(SumAsLongSimulator())
            registerExpressionSimulator(SumAsDoubleSimulator())
            registerExpressionSimulator(SumAsBigDecimalSimulator())
            registerExpressionSimulator(SubqueryExpressionSimulator<E, Any?>(queryEvaluator))
        }
    }

    /**
     * Template method to implement to construct the actual [EngineSimulator] instance.
     *
     * @return the [EngineSimulator] instance
     */
    protected abstract fun makeSimulator(): S

    /**
     * Builds a [GenericEngineSimulator] with the configured components.
     *
     * Do not call twice because of side effects.
     *
     * @return a [GenericEngineSimulator] with the configured components
     */
    fun build(): S {
        check(!consumed) { "Cannot build more than one GenericEngineSimulator." }.also { consumed = true }
        require(queryEvaluator.expressionEvaluatorForChecking == expressionEvaluator) { "QueryEvaluator must use no other than the Engine-wide ExpressionEvaluator." }
        registerExpressionSimulators()
        return makeSimulator()
    }
}
