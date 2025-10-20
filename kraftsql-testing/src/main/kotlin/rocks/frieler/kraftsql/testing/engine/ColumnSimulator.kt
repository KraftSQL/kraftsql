package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.sql.SQLSyntaxErrorException
import kotlin.reflect.KClass

/**
 * Simulator for [Column] expressions.
 *
 * @param <E> the [Engine] to simulate
 * @param <T> the Kotlin type of the [Column] and thereby the return type of its simulation
 */
open class ColumnSimulator<E : Engine<E>, T : Any> : ExpressionSimulator<E, T, Column<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Column::class as KClass<out Column<E, T>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Column<E, T>): (DataRow) -> T? = { row ->
        @Suppress("UNCHECKED_CAST")
        row[expression.qualifiedName] as T
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Column<E, T>): (List<DataRow>) -> T? {
        if (expression in groupExpressions) {
            return { rows -> simulateExpression(expression)(rows.first()) }
        }

        throw SQLSyntaxErrorException("'${expression.sql()}' is neither in the GROUP BY list nor wrapped in an aggregation.")
    }
}
