package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class IsNotNullTest {
    @Test
    fun `IsNotNull renders SQL for expression 'IS NOT NULL'`() {
        val expression = mock<Expression<TestableDummyEngine, *>> {
            whenever(it.sql()).thenReturn("e")
        }

        IsNotNull(expression).sql() shouldBe "e IS NOT NULL"
    }

}
