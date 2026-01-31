package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.ArrayConcatenation
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass

/**
 * Simulator for [ArrayConcatenation] expressions.
 *
 * Note: An [ArrayConcatenation] expression is not part of standard SQL and not supported by the generic simulator by
 * default. [Register it][GenericSimulatorConnection.registerExpressionSimulator], when you want to support this
 * [ArrayConcatenation].
 *
 * See [simulate] for the behavior simulated for [ArrayConcatenation] - and override it if necessary.
 *
 * @param E the [Engine] to simulate
 * @param T the Kotlin type of the arrays' elements
 */
open class ArrayConcatenationSimulator<E : Engine<E>, T>(
    override val expression : KClass<out ArrayConcatenation<E, T>>
) : ExpressionSimulator<E, Array<T>?, ArrayConcatenation<E,T>> {
    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: ArrayConcatenation<E, T>): (DataRow) -> Array<T>? {
        val argumentSimulations = expression.arguments.map { subexpressionCallbacks.simulateExpression(it) }
        return { row -> simulate(argumentSimulations.map { it(row) }) }
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: ArrayConcatenation<E, T>): (List<DataRow>) -> Array<T>? {
        val argumentSimulations = expression.arguments.map { subexpressionCallbacks.simulateAggregation(it) }
        return { rows -> simulate(argumentSimulations.map { it(rows) }) }
    }

    /**
     * Concatenates the given [Array]s into a single [Array]. If any of them is `null`, the result is `null`.
     *
     * @param arrays the [Array]s to concatenate
     * @return an [Array] with the content of the `arrays` concatenated
     */
    protected fun simulate(arrays: List<Array<T>?>): Array<T>? =
        arrays.reduce { acc, ts -> if (acc != null && ts != null) acc + ts else null }
}
