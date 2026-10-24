package rocks.frieler.kraftsql.testing

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.ConstantData
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.collect
import rocks.frieler.kraftsql.local.engine.KraftSqlLocalEngine

@WithKraftSqlLocalEngine
class WithKraftSqlLocalEngineTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setOtherDefaultEngine() {
            Engine.setDefault(mockk())
        }

        @JvmStatic
        @AfterAll
        fun resetDefaultEngine() {
            Engine.resetDefault()
        }
    }

    private val engineDuringConstruction = Engine.default

    @Test
    fun `KraftSqlLocalEngine is the default Engine already during construction of the test instance`() {
        engineDuringConstruction.shouldBeInstanceOf<KraftSqlLocalEngine>()
    }

    @Test
    fun `Data can be collected with the default Engine`() {
        data class Answer(val answer: Int)

        val answers = ConstantData(Answer(42)).collect()

        answers shouldContainExactly listOf(Answer(42))
    }
}
