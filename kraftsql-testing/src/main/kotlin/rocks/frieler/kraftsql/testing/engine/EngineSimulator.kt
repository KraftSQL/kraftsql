package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine

interface EngineSimulator<E : Engine<E>> {
    val persistentState: EngineState<E>
    val expressionEvaluator: GenericExpressionEvaluator<E>
    val queryEvaluator: GenericQueryEvaluator<E>
}
