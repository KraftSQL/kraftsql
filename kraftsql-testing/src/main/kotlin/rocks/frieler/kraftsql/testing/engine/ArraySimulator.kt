package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf

/**
 * Simulator for [Array] expressions.
 *
 * @param <E> the [Engine] to simulate
 * @param <T> the Kotlin type of the [Array]s elements, which defines the return type of the is simulation to be `Array<T?>`
 */
class ArraySimulator<E : Engine<E>, T : Any> : ExpressionSimulator<E, kotlin.Array<T?>, Array<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Array::class as KClass<out Array<E, T>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Array<E, T>): (DataRow) -> kotlin.Array<T?>? = { row ->
        simulate(expression.elements?.map { subexpressionCallbacks.simulateExpression(it)(row) })
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Array<E, T>): (List<DataRow>) -> kotlin.Array<T?>? = { rows ->
        simulate(expression.elements?.map { subexpressionCallbacks.simulateAggregation(it)(rows) })
    }

    private fun <T : Any> simulate(elements: List<T?>?): kotlin.Array<T?>? =
        if (elements == null) {
            null
        } else {
            val commonSuperType = elements.filterNotNull()
                .map { setOf(it::class) + it::class.allSuperclasses }
                .run { reduceOrNull { classes1, classes2 -> classes1.intersect(classes2) } ?: emptySet() }
                .let { candidates -> candidates.filter { candidate -> !candidates.all { other -> other != candidate && other.isSubclassOf(candidate) } } }
                .firstOrNull() ?: Any::class
            @Suppress("UNCHECKED_CAST")
            (java.lang.reflect.Array.newInstance(commonSuperType.java, elements.size) as kotlin.Array<T?>).also { array ->
                elements.forEachIndexed { index, element -> (array)[index] = element }
            }
        }
}
