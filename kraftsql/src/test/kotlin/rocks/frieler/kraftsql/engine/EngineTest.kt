package rocks.frieler.kraftsql.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.Data
import rocks.frieler.kraftsql.data.DataRow
import java.util.ServiceLoader

class EngineTest {
    @AfterEach
    fun resetDefaultEngine() {
        Engine.resetDefault()
    }

    @Test
    fun `default Engine is loaded from a single registered Engine implementation`() {
        val theOnlyEngineImplementation = mockk<Engine>()
        val engineLoader = mockk<ServiceLoader<Engine>> { every { iterator() } answers { mutableListOf(theOnlyEngineImplementation).iterator() } }
        mockkStatic(ServiceLoader::class) {
            every { ServiceLoader.load(Engine::class.java) } returns engineLoader

            Engine.default shouldBe theOnlyEngineImplementation
        }
    }

    @Test
    fun `default Engine is loaded once and cached`() {
        val theOnlyEngineImplementation = mockk<Engine>()
        val engineLoader = mockk<ServiceLoader<Engine>> { every { iterator() } answers { mutableListOf(theOnlyEngineImplementation).iterator() } }
        mockkStatic(ServiceLoader::class) {
            every { ServiceLoader.load(Engine::class.java) } returns engineLoader

            Engine.default shouldBe theOnlyEngineImplementation
            Engine.default shouldBe theOnlyEngineImplementation
            verify(exactly = 1) { ServiceLoader.load(Engine::class.java) }
        }
    }

    @Test
    fun `default Engine is not available when there is no registered Engine implementation`() {
        val engineLoader = mockk<ServiceLoader<Engine>> { every { iterator() } answers { mutableListOf<Engine>().iterator() } }
        mockkStatic(ServiceLoader::class) {
            every { ServiceLoader.load(Engine::class.java) } returns engineLoader

            shouldThrow<IllegalStateException> { Engine.default }
        }
    }

    @Test
    fun `default Engine is ambiguous when there is more than one registered Engine implementation`() {
        val engineLoader = mockk<ServiceLoader<Engine>> { every { iterator() } answers { mutableListOf(mockk<Engine>(), mockk<Engine>()).iterator() } }
        mockkStatic(ServiceLoader::class) {
            every { ServiceLoader.load(Engine::class.java) } returns engineLoader

            shouldThrow<IllegalStateException> { Engine.default }
        }
    }

    @Test
    fun `Data#collect() uses default Engine`() {
        val defaultEngine = mockk<Engine>().also { Engine.setDefault(it) }
        val data = mockk<Data<DataRow>>()
        val row = mockk<DataRow>()
        every { defaultEngine.collect(data, DataRow::class) } returns listOf(row)

        data.collect() shouldBe listOf(row)
    }
}
