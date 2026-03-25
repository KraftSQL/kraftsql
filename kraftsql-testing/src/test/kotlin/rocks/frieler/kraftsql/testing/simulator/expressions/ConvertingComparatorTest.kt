package rocks.frieler.kraftsql.testing.simulator.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.SQLException
import kotlin.time.Instant

class ConvertingComparatorTest {
    private val comparator = ConvertingComparator()

    @Test
    fun `compare returns value less than 0 if first value is less than second value`() {
        comparator.compare(1, 2) shouldNotBeNull {
            this shouldBeLessThan 0
        }
    }

    @Test
    fun `compare returns 0 if first value and second value are equal`() {
        comparator.compare(1, 1) shouldNotBeNull {
            this shouldBeEqual 0
        }
    }
    @Test
    fun `compare returns value greater than 0 if first value is greater than second value`() {
        comparator.compare(2, 1) shouldNotBeNull {
            this shouldBeGreaterThan 0
        }
    }

    @Test
    fun `compare returns null if any value is null`() {
        comparator.compare(null, 1) shouldBe null
        comparator.compare(1, null) shouldBe null
        comparator.compare(null, null) shouldBe null
    }

    @Test
    fun `compare can compare any two Numbers`() {
        val numbersOfAllTypes = listOf(0.toByte(), 0.toShort(), 0, 0L, BigInteger.ZERO, 0f, 0.0, BigDecimal.ZERO)

        for (number1 in numbersOfAllTypes) {
            for (number2 in numbersOfAllTypes) {
                comparator.compare(number1, number2) shouldNotBe null
            }
        }
    }

    @Test
    fun `compare can compare two Strings`() {
        comparator.compare("a", "b") shouldNotBeNull {
            this shouldBeLessThan 0
        }
    }

    @Test
    fun `compare can compare String and Number`() {
        val numbersOfAllTypes = listOf(0.toByte(), 0.toShort(), 0, 0L, BigInteger.ZERO, 0f, 0.0, BigDecimal.ZERO)

        for (number in numbersOfAllTypes) {
            comparator.compare("a", number) shouldNotBe null
            comparator.compare(number, "a") shouldNotBe null
        }
    }

    @Test
    fun `compare can compare two Instants`() {
        comparator.compare(Instant.DISTANT_PAST, Instant.DISTANT_FUTURE) shouldNotBe null
    }

    @Test
    fun `compare rejects to compare incompatible types`() {
        shouldThrow<SQLException> {
            comparator.compare(Any(), Any())
        }
    }
}
