package rocks.frieler.kraftsql.testing.simulator.expressions

import java.math.BigDecimal
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
                BigDecimal.valueOf(value1.toLong()).compareTo(BigDecimal.valueOf(value2.toLong()))
            }
            value1 is String && (value2 is String || value2 is Number) || value2 is String && value1 is Number -> {
                value1.toString().compareTo(value2.toString())
            }
            value1 is Instant && value2 is Instant -> value1.compareTo(value2)
            else -> throw SQLException("$value1 and $value2 are not compatible for comparison.")
        }
}
