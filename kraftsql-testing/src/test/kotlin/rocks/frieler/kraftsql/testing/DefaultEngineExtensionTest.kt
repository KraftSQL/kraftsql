package rocks.frieler.kraftsql.testing

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.engine.Engine

class DefaultEngineExtensionTest {
    private val engine = mockk<Engine>()
    private val extension = DefaultEngineExtension { engine }

    @AfterEach
    fun resetDefaultEngine() {
        Engine.resetDefault()
    }

    @Test
    fun `preConstructTestInstance() sets the provided Engine as default`() {
        extension.preConstructTestInstance(mockk(), mockk())

        Engine.default shouldBe engine
    }

    @Test
    fun `preDestroyTestInstance() resets the default Engine`() {
        extension.preConstructTestInstance(mockk(), mockk())

        extension.preDestroyTestInstance(mockk())

        Engine.default shouldNotBeSameInstanceAs engine
    }
}
