package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.DataRow
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.cast
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

/**
 * Simulator for the [Cast] function.
 *
 * @param <E> the [Engine] to simulate
 * @param <T> the Kotlin type of the [Cast]s target type and thereby the return type of its simulation
 */
class CastSimulator<E : Engine<E>, T : Any> : ExpressionSimulator<E, T?, Cast<E, T>> {
    @Suppress("UNCHECKED_CAST")
    override val expression = Cast::class as KClass<out Cast<E, T>>

    context(subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateExpression(expression: Cast<E, T>): (DataRow) -> T? = { row ->
        val value = subexpressionCallbacks.simulateExpression(expression.expression)(row)
        val targetType = expression.type.naturalKType()
        simulate(value, targetType)
    }

    context(groupExpressions: List<Expression<E, *>>, subexpressionCallbacks: ExpressionSimulator.SubexpressionCallbacks<E>)
    override fun simulateAggregation(expression: Cast<E, T>): (List<DataRow>) -> T? = { rows ->
        val value = subexpressionCallbacks.simulateAggregation(expression.expression)(rows)
        val targetType = expression.type.naturalKType()
        simulate(value, targetType)
    }

    @Suppress("UNCHECKED_CAST")
    private fun simulate(value: Any?, targetType: KType): T? =
        when (targetType) {
            typeOf<Boolean>() -> value?.toString()?.toBooleanStrictOrNull()
            typeOf<Int>() -> value?.toString()?.toInt()
            typeOf<Long>() -> value?.toString()?.toLong()
            typeOf<String>() -> value?.toString()
            typeOf<LocalDate>() -> value?.toString()?.let { LocalDate.parse(it) }
            // TODO: add support for other types as needed
            else -> targetType.jvmErasure.cast(value)
        } as T?
}
