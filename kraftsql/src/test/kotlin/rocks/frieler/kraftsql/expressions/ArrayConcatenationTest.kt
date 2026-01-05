package rocks.frieler.kraftsql.expressions

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import kotlin.Array

class ArrayConcatenationTest {
    private class TestableArrayConcatenation<T>(arguments: Array<Expression<TestableDummyEngine, Array<T>?>>) : ArrayConcatenation<TestableDummyEngine, T>(arguments) {
        override fun sql(): String = TODO("Not yet implemented")
        override fun defaultColumnName(): String = TODO("Not yet implemented")
    }

    @Test
    fun `ArrayConcatenations with same arguments are equal`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any>?>>()
        val anotherArrayExpression = mock<Expression<TestableDummyEngine, Array<Any>?>>()

        val arrayConcatenation = TestableArrayConcatenation(arrayOf(arrayExpression, anotherArrayExpression))

        TestableArrayConcatenation(arrayConcatenation.arguments) shouldBeEqual arrayConcatenation
    }

    @Test
    fun `ArrayConcatenations with different arguments are not equal`() {
        val arrayConcatenation = TestableArrayConcatenation(arrayOf(mock<Expression<TestableDummyEngine, Array<Any>?>>()))
        val anotherArrayConcatenation = TestableArrayConcatenation(arrayOf(mock<Expression<TestableDummyEngine, Array<Any>?>>()))

        arrayConcatenation shouldNotBeEqual anotherArrayConcatenation
    }

    @Test
    fun `ArrayConcatenations and something else are not equal`() {
        val arrayConcatenation = TestableArrayConcatenation(arrayOf(mock<Expression<TestableDummyEngine, Array<Any>?>>()))

        arrayConcatenation shouldNotBeEqual Any()
    }

    @Test
    fun `equal ArrayConcatenations have the same hash code`() {
        val arrayExpression = mock<Expression<TestableDummyEngine, Array<Any>?>>()
        val anotherArrayExpression = mock<Expression<TestableDummyEngine, Array<Any>?>>()

        val arrayConcatenation = TestableArrayConcatenation(arrayOf(arrayExpression, anotherArrayExpression))

        TestableArrayConcatenation(arrayConcatenation.arguments).hashCode() shouldBe arrayConcatenation.hashCode()
    }
}
