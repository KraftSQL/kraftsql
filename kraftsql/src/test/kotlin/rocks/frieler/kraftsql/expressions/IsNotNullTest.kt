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

    @Test
    fun `IsNotNull's default column name is expression's default column name with '_IS_NOT_NULL' suffix`() {
        val expression = mock<Expression<TestableDummyEngine, *>> {
            whenever(it.defaultColumnName()).thenReturn("e")
        }

        IsNotNull(expression).defaultColumnName() shouldBe "e_IS_NOT_NULL"
    }
}
