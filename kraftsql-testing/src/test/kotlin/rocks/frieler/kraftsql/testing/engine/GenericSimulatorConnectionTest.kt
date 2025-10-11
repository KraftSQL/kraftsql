package rocks.frieler.kraftsql.testing.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import kotlin.reflect.typeOf

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

    @Test
    fun `GenericSimulatorConnection can simulate a Column expression`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(SimulatorORMapping(), DataRow(mapOf("foo" to "bar")))),
                columns = listOf(Projection(Column("foo"))),
            ), DataRow::class
        )

        result.single()["foo"] shouldBe "bar"
    }

    @Test
    fun `GenericSimulatorConnection can simulate a Cast`() {
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }

        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(SimulatorORMapping(), DataRow(emptyMap()))),
                columns = listOf(Projection(Cast(Constant("123"), intType), "number")),
            ), DataRow::class
        )

        result.single()["number"] shouldBe 123
    }

    @Test
    fun `GenericSimulatorConnection can simulate the equals-operator`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(SimulatorORMapping(), DataRow(emptyMap()))),
                columns = listOf(Projection(Equals(Constant(1), Constant(1)), "equals")),
            ), DataRow::class
        )

        result.single()["equals"] shouldBe true
    }
}
