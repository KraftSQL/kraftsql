package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator

/**
 * Generic, configurable base implementation of an [EngineSimulator].
 *
 * @param E the [Engine] to simulate
 * @param persistentState the [EngineState] that holds the persisted data
 * @param expressionEvaluator the [GenericExpressionEvaluator] to evaluate expressions supported by the [Engine]
 * @param queryEvaluator the [GenericQueryEvaluator] to evaluate queries as supported by the [Engine]
 */
open class GenericEngineSimulator<E : Engine<E>>(
    override val persistentState: EngineState<E> = EngineState(),
    override val expressionEvaluator: GenericExpressionEvaluator<E> = GenericExpressionEvaluator(),
    override val queryEvaluator: GenericQueryEvaluator<E> = GenericQueryEvaluator(expressionEvaluator = expressionEvaluator),
) : EngineSimulator<E> {
    init {
        require(queryEvaluator.expressionEvaluatorForChecking == expressionEvaluator) { "QueryEvaluator must use no other than the Engine-wide ExpressionEvaluator." }
    }
}
