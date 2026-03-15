package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.engine.EngineState
import rocks.frieler.kraftsql.testing.engine.GenericQueryEvaluator

object H2QueryEvaluator : GenericQueryEvaluator<H2Engine>(
    orm = H2SimulatorORMapping,
    expressionEvaluator = H2ExpressionEvaluator,
) {
    context(activeState : EngineState<H2Engine>)
    override fun fetchData(data: Data<*>, correlatedData: DataRow?) =
        when (data) {
            is SystemRange -> (expressionEvaluator.simulateExpression(data.from)(DataRow())..expressionEvaluator.simulateExpression(data.to)(DataRow())).map { DataRow("X" to it) }
            else -> super.fetchData(data, correlatedData)
        }
}
