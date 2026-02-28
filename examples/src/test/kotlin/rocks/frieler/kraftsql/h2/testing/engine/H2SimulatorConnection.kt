package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.engine.ArrayConcatenationSimulator
import rocks.frieler.kraftsql.testing.engine.GenericSimulatorConnection
import kotlin.reflect.KClass

class H2SimulatorConnection : GenericSimulatorConnection<H2Engine>(orm = H2SimulatorORMapping) {

    override fun fetchData(data: Data<*>, correlatedData: DataRow?) =
        when (data) {
            is SystemRange -> (simulateExpression(data.from)(DataRow())..simulateExpression(data.to)(DataRow())).map { DataRow("X" to it) }
            else -> super.fetchData(data, correlatedData)
        }

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
