package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.TestableDummyEngine

class SubqueryExpressionTest {
    @Test
    fun `SQL wraps subquery SQL in parentheses`() {
        val subquery = mock<Select<TestableDummyEngine, Any>> { whenever(it.sql()).thenReturn("SELECT x FROM t") }

        val expression = SubqueryExpression<TestableDummyEngine, Any>(subquery)

        expression.sql() shouldBe "(SELECT x FROM t)"
    }

    @Test
    fun `SubqueryExpression with equal subquery is equal`() {
        val subquery = mock<Select<TestableDummyEngine, Any>>()

        SubqueryExpression<TestableDummyEngine, Any>(subquery) shouldBeEqual SubqueryExpression(subquery)
    }

    @Test
    fun `SubqueryExpression with different subquery is not equal`() {
        val subquery1 = mock<Select<TestableDummyEngine, Any>>()
        val subquery2 = mock<Select<TestableDummyEngine, Any>>()

        SubqueryExpression<TestableDummyEngine, Any>(subquery1) shouldNotBeEqual SubqueryExpression(subquery2)
    }

    @Test
    fun `SubqueryExpression and something else are not equal`() {
        SubqueryExpression<TestableDummyEngine, Any>(mock()) shouldNotBeEqual Any()
    }

    @Test
    fun `SubqueryExpression with equal subquery has same hash code`() {
        val subquery = mock<Select<TestableDummyEngine, Any>>()

        SubqueryExpression<TestableDummyEngine, Any>(subquery).hashCode() shouldBeEqual SubqueryExpression<TestableDummyEngine, Any>(subquery).hashCode()
    }
}
