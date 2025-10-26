package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class ExpressionTest {
    @Test
    fun `knownNotNull casts Expression to non-nullable value type`() {
        val expression = mock<Expression<TestableDummyEngine, Any?>>()

        val nonNullableExpression : Expression<TestableDummyEngine, Any> = expression.knownNotNull()

        nonNullableExpression shouldBeSameInstanceAs expression
    }
}
