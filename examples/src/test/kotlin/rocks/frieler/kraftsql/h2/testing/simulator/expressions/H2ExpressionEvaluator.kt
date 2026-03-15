package rocks.frieler.kraftsql.h2.testing.simulator.expressions

import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.testing.simulator.expressions.ArrayConcatenationSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator
import kotlin.reflect.KClass

object H2ExpressionEvaluator : GenericExpressionEvaluator<H2Engine>() {
    init {
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
