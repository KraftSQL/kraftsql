package rocks.frieler.kraftsql.h2.testing.simulator.engine

import rocks.frieler.kraftsql.expressions.And
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.ArrayElementReference
import rocks.frieler.kraftsql.expressions.ArrayLength
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.expressions.LessOrEqual
import rocks.frieler.kraftsql.expressions.Max
import rocks.frieler.kraftsql.expressions.Min
import rocks.frieler.kraftsql.expressions.Not
import rocks.frieler.kraftsql.expressions.Or
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.expressions.Sum
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.h2.expressions.Column
import rocks.frieler.kraftsql.h2.expressions.SystemRange
import rocks.frieler.kraftsql.h2.objects.Data
import rocks.frieler.kraftsql.h2.testing.simulator.expressions.H2ExpressionEvaluator
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.engine.EngineState
import rocks.frieler.kraftsql.testing.simulator.engine.GenericQueryEvaluator

object H2QueryEvaluator : GenericQueryEvaluator<H2Engine>(
    orm = H2SimulatorORMapping,
    expressionEvaluator = H2ExpressionEvaluator,
) {

    override fun makeColumnReference(columnName: String) = Column<Any?>(columnName)

    context(activeState : EngineState<H2Engine>)
    override fun fetchRows(data: Data<*>, correlatedData: DataRow?) =
        when (data) {
            is SystemRange -> (expressionEvaluator.simulateExpression(data.from)(DataRow())..expressionEvaluator.simulateExpression(data.to)(DataRow())).map { DataRow("X" to it) }
            else -> super.fetchRows(data, correlatedData)
        }

    override fun fillColumnNames(columns: List<Pair<String?, Expression<H2Engine, *>>>): List<Pair<String, Expression<H2Engine, *>>> =
        columns.map { (alias, expression) -> (alias ?: expression.defaultColumnName()) to expression }

    private fun Expression<H2Engine, *>.defaultColumnName(): String = when (this) {
        is And -> "${left.defaultColumnName()}_AND_${right.defaultColumnName()}"
        is Array<H2Engine, *> -> elements?.joinToString(",", prefix = "[", postfix = "]") { it.defaultColumnName() } ?: "NULL"
        is ArrayConcatenation<*> -> "${left.defaultColumnName()} || ${right.defaultColumnName()}"
        is ArrayElementReference -> "${array.defaultColumnName()}[${index.defaultColumnName()}]"
        is ArrayLength -> "ARRAY_LENGTH(${array.defaultColumnName()})"
        is Cast -> "CAST(${expression.defaultColumnName()} AS ${type.sql()})"
        is Coalesce -> "COALESCE(${expressions.joinToString(",") { it.defaultColumnName() }})"
        is Column<*> -> qualifiedName
        is Constant -> sql()
        is Count -> "COUNT(${expression?.defaultColumnName() ?: "*"})"
        is Equals -> "${left.defaultColumnName()} = ${right.defaultColumnName()}"
        is IsNotNull -> "${expression.defaultColumnName()}_IS_NOT_NULL"
        is LessOrEqual -> "${left.defaultColumnName()}<=${right.defaultColumnName()}"
        is Max<H2Engine, *> -> "MAX(${expression.defaultColumnName()})"
        is Min<H2Engine, *> -> "MIN(${expression.defaultColumnName()})"
        is Not -> "NOT_${expression.defaultColumnName()}"
        is Or -> "${left.defaultColumnName()}_OR_${right.defaultColumnName()}"
        is Row<H2Engine, *> -> values?.entries?.joinToString(",") { (key, value) -> "$key:${value.defaultColumnName()}" } ?: "NULL"
        is Sum<H2Engine, *> -> "SUM(${expression.defaultColumnName()})"
        else -> throw NotImplementedError("Generating a column name for ${this::class.qualifiedName} is not implemented.")
    }

    override fun inferColumns(data: Data<*>) = when (data) {
        is SystemRange -> listOf("X")
        else -> super.inferColumns(data)
    }
}
