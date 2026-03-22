package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.objects.DataRow

class SimulatorORMappingTest {
    private val orm = SimulatorORMapping<DummyEngine>()

    @Test
    fun `deserializeQueryResult can deserialize DataRow into data class`() {
        data class Something(val id: Int, val value: String)

        val result = orm.deserializeQueryResult(listOf(DataRow("id" to 42, "value" to "answer")), Something::class)

        result shouldContainExactlyInAnyOrder listOf(Something(42, "answer"))
    }
}
