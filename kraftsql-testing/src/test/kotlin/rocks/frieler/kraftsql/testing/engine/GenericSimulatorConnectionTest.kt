package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.expressions.Array
import rocks.frieler.kraftsql.expressions.Cast
import rocks.frieler.kraftsql.expressions.Coalesce
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Count
import rocks.frieler.kraftsql.expressions.Equals
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.IsNotNull
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import kotlin.reflect.typeOf

class GenericSimulatorConnectionTest {
    private val connection = GenericSimulatorConnection<DummyEngine>()

    @Test
    fun `GenericSimulatorConnection can simulate a Constant expression`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Constant(42L))),
            ), DataRow::class
        )

        result.single().entries.single().second shouldBe 42L
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT from source with alias`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow("answer" to 42L)), "data"),
                columns = listOf(Projection(Column<DummyEngine, Long>("data.answer"))),
            ), DataRow::class
        )

        result.single()["data.answer"] shouldBe 42L
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT of all columns`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow("integer" to 42, "string" to "foo"))),
            ), DataRow::class
        )

        result.single()["integer"] shouldBe 42
        result.single()["string"] shouldBe "foo"
    }

    @Test
    fun `GenericSimulatorConnection selects columns from source and joins when selecting all columns`() {

        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow("left" to "foo"))),
                joins = listOf(
                    InnerJoin(QuerySource(ConstantData(DummyEngine.orm, DataRow("right" to "bar"))), Constant(true)),
                )
            ), DataRow::class)

        result.single().columnNames shouldContainExactly listOf("left", "right")
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT of all columns when grouping`() {
        val dummyData = ConstantData(DummyEngine.orm,
            DataRow("integer" to 42, "string" to "foo"),
            DataRow("integer" to 43, "string" to "bar"),
            DataRow("integer" to 43, "string" to "baz"),
        )

        val result = connection.execute(
            Select(
                source = QuerySource(dummyData),
                grouping = listOf(dummyData["integer"]),
            ), DataRow::class
        )

        result shouldHaveSize 2
        result.forEach { row ->
            row.columnNames shouldBe listOf("integer")
        }
    }

    @Test
    fun `GenericSimulatorConnection can simulate InsertInto`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT),
        )).also { connection.execute(CreateTable(it)) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to "foo"))

        val rows = connection.execute(InsertInto(table, testData))

        rows shouldBe 1
    }

    @Test
    fun `GenericSimulatorConnection rejects inserting data that does not match the Table's schema`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c1", DummyEngine.Types.TEXT),
            rocks.frieler.kraftsql.objects.Column("c2", DummyEngine.Types.TEXT),
        )).also { connection.execute(CreateTable(it)) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c1" to null, "col" to null))

        shouldThrow<IllegalArgumentException> {
            connection.execute(InsertInto(table, testData))
        }
    }

    @Test
    fun `GenericSimulatorConnection rejects inserting NULL into non-nullable column`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(
            rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT, nullable = false),
        )).also { connection.execute(CreateTable(it)) }
        val testData = ConstantData(DummyEngine.orm, DataRow("c" to null))

        shouldThrow<IllegalArgumentException> {
            connection.execute(InsertInto(table, testData))
        }
    }

    @Test
    fun `GenericSimulatorConnection can simulate a Column expression`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow("foo" to "bar"))),
                columns = listOf(Projection(Column<DummyEngine, String>("foo"))),
            ), DataRow::class
        )

        result.single()["foo"] shouldBe "bar"
    }

    @Test
    fun `GenericSimulatorConnection can simulate a Cast`() {
        val intType = mock<Type<DummyEngine, Int>> { whenever(it.naturalKType()).thenReturn(typeOf<Int>()) }

        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Cast(Constant("123"), intType), "number")),
            ), DataRow::class
        )

        result.single()["number"] shouldBe 123
    }

    @Test
    fun `GenericSimulatorConnection can simulate the IS NOT NULL operator`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(IsNotNull(Constant(1)), "not_null")),
            ), DataRow::class
        )

        result.single()["not_null"] shouldBe true
    }

    @Test
    fun `GenericSimulatorConnection can simulate the equals-operator`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Equals(Constant(1), Constant(1)), "equals")),
            ), DataRow::class
        )

        result.single()["equals"] shouldBe true
    }

    @Test
    fun `GenericSimulatorConnection can simulate the COALESCE function`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Coalesce(Constant(null), Constant(42L)), "answer")),
            ), DataRow::class
        )

        result.single()["answer"] shouldBe 42L
    }

    @Test
    fun `GenericSimulatorConnection can simulate an Array expression`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Array(Constant(1), Constant(2)), "array")),
            ), DataRow::class
        )

        result.single()["array"] shouldBe arrayOf(1, 2)
    }

    @Test
    fun `GenericSimulatorConnection can simulate a Row expression`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(Row<DummyEngine, DataRow>(mapOf("key" to Constant(1), "value" to Constant("foo"))), "row")),
            ), DataRow::class
        )

        result.single()["row"] shouldBe DataRow("key" to 1, "value" to "foo")
    }

    @Test
    fun `GenericSimulatorConnection rejects to simulate an unknown Expression`() {
        shouldThrow<NotImplementedError> {
            connection.execute(
                Select(
                    source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                    columns = listOf(Projection(mock<Expression<DummyEngine, Nothing>>(), "_")),
                ), DataRow::class
            )
        }
    }

    @Test
    fun `GenericSimulatorConnection can simulate a Count aggregation`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                grouping = listOf(Constant(1)),
                columns = listOf(Projection(Count(), "count")),
            ), DataRow::class
        )

        result.single()["count"] shouldBe 1
    }
}
