package rocks.frieler.kraftsql.testing.simulator.expressions

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.SQLException
import kotlin.time.Instant

/**
 * Comparator that can convert values to a common type for comparison according to general SQL behavior.
 */
open class ConvertingComparator {
    open fun compare(value1: Any?, value2: Any?): Int? =
        when {
            value1 == null || value2 == null -> null
            value1 is Number && value2 is Number -> {
                value1.toBigDecimal().compareTo(value2.toBigDecimal())
            }
            value1 is String && (value2 is String || value2 is Number) || value2 is String && value1 is Number -> {
                value1.toString().compareTo(value2.toString())
            }
            value1 is Instant && value2 is Instant -> value1.compareTo(value2)
            else -> throw SQLException("$value1 and $value2 are not compatible for comparison.")
        }

    private fun Number.toBigDecimal() = when (this) {
        is Byte -> BigDecimal.valueOf(toLong())
        is Short -> BigDecimal.valueOf(toLong())
        is Int -> BigDecimal.valueOf(toLong())
        is Long -> BigDecimal.valueOf(this)
        is BigInteger -> this.toBigDecimal(scale = 0)
        is Float -> BigDecimal.valueOf(toDouble())
        is Double -> BigDecimal.valueOf(this)
        is BigDecimal -> this
        else -> throw NotImplementedError("${this::class.qualifiedName} cannot be converted for comparison.")
    }
}
