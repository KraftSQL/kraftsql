package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow

class GenericSimulatorConnectionTest {
    private val connection = GenericSimulatorConnection<DummyEngine>()

    @Test
    fun `GenericSimulatorConnection can simulate a Constant expression`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(SimulatorORMapping(), DataRow(emptyMap()))),
                columns = listOf(Projection(Constant(42L))),
            ), DataRow::class
        )

        result.single().values.values.single() shouldBe 42L
    }
}
