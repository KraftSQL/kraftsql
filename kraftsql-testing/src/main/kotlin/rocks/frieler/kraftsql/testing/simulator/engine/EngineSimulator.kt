package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator

/**
 * Interface for a simulator of an [Engine].
 *
 * @param E the [Engine] to simulate
 */
interface EngineSimulator<E : Engine<E>> {
    val persistentState: EngineState<E>
    val expressionEvaluator: GenericExpressionEvaluator<E>
    val queryEvaluator: GenericQueryEvaluator<E>
}
