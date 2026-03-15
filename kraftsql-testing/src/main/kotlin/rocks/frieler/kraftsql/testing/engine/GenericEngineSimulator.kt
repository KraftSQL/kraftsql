package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine

open class GenericEngineSimulator<E : Engine<E>>(
    override val persistentState: EngineState<E> = EngineState(),
    override val expressionEvaluator: GenericExpressionEvaluator<E> = GenericExpressionEvaluator(),
    override val queryEvaluator: GenericQueryEvaluator<E> = GenericQueryEvaluator(expressionEvaluator = expressionEvaluator),
) : EngineSimulator<E> {
    init {
        require(queryEvaluator.expressionEvaluatorForChecking == expressionEvaluator) { "QueryEvaluator must use no other than the Engine-wide ExpressionEvaluator." }
    }
}
