package rocks.frieler.kraftsql.testing.kotest.inspectors

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test

class FilterForOneTest {
    @Test
    fun `filterForOne() returns single matching element`() {
        val matchingElement = listOf("Hello", "KraftSQL").filterForOne { it shouldContain "Kraft" }

        matchingElement shouldBe "KraftSQL"
    }
}
