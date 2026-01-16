package rocks.frieler.kraftsql.dql

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Expression

class JoinsTest {
    private val right = mock<QuerySource<TestableDummyEngine, *>> { whenever(it.sql()).thenReturn("right") }
    private val condition = mock<Expression<TestableDummyEngine, Boolean>> { whenever(it.sql()).thenReturn("condition") }

    @Test
    fun `InnerJoin renders INNER JOIN SQL`() {
        InnerJoin(right, condition).sql() shouldBe "INNER JOIN right ON condition"
    }

    @Test
    fun `LeftJoin renders LEFT JOIN SQL`() {
        LeftJoin(right, condition).sql() shouldBe "LEFT JOIN right ON condition"
    }

    @Test
    fun `RightJoin renders RIGHT JOIN SQL`() {
        RightJoin(right, condition).sql() shouldBe "RIGHT JOIN right ON condition"
    }
}
