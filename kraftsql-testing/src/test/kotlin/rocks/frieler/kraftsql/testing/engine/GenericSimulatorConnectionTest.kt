package rocks.frieler.kraftsql.testing.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dql.CrossJoin
import rocks.frieler.kraftsql.dql.DataExpressionData
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.LeftJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.RightJoin
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.And
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
import rocks.frieler.kraftsql.objects.Data
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
    fun `GenericSimulatorConnection can simulate SELECT from primitive ConstantData`() {
        val result = connection.execute(
            Select(source = QuerySource(ConstantData(DummyEngine.orm, 1, 2, 3))),
            DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(DataRow("" to 1), DataRow("" to 2), DataRow("" to 3))
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
    fun `Empty column names of a QuerySource are just named by the alias`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow("" to 42L)), "answer"),
                columns = listOf(Projection(Column<DummyEngine, Long>("answer"))),
            ), DataRow::class
        )

        result.single()["answer"] shouldBe 42L
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT from table`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT)))

        connection.execute(CreateTable(table))
        connection.execute(InsertInto(table, ConstantData(DummyEngine.orm, DataRow("c" to "foo"))))
        val result = connection.execute(Select(source = QuerySource(table)), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(DataRow("c" to "foo"))
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT from empty table`() {
        val table = Table<DummyEngine, DataRow>("unit-tests", "test-data", "table", listOf(rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT)))

        connection.execute(CreateTable(table))
        val result = connection.execute(Select(source = QuerySource(table)), DataRow::class)

        result shouldBe emptyList()
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT from sub-query`() {
        val result = connection.execute(
            Select(
                source = QuerySource(Select(source = QuerySource(ConstantData(DummyEngine.orm, DataRow("c" to "foo")))))
            ), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(DataRow("c" to "foo"))
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT from empty sub-query`() {
        val result = connection.execute(
            Select(
                source = QuerySource(Select(source = QuerySource(ConstantData.empty(DummyEngine.orm, listOf("c")))))
            ), DataRow::class)

        result shouldBe emptyList()
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT of certain columns of empty data`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData.empty(DummyEngine.orm, listOf("c1", "c2"))),
                columns = listOf(Projection(Column<DummyEngine, String>("c1")))
            ), DataRow::class)

        result shouldBe emptyList()
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
    fun `GenericSimulatorConnection can simulate INNER JOIN`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("key" to  41, "entity" to "x"),
                    DataRow("key" to  42, "entity" to "y"),
                ), "left"),
                joins = listOf(
                    InnerJoin(
                        QuerySource(ConstantData(DummyEngine.orm,
                            DataRow("key" to 42, "attribute" to "foo"),
                            DataRow("key" to 43, "attribute" to "bar"),
                        ), "right"),
                        Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key"))
                )
            ), DataRow::class)

        result shouldContainExactly listOf(
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to 42, "right.attribute" to "foo"),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate INNER JOIN with empty result`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("key" to  41, "entity" to "x"),
                ), "left"),
                joins = listOf(
                    InnerJoin(
                        QuerySource(ConstantData(DummyEngine.orm,
                            DataRow("key" to 43, "attribute" to "bar"),
                        ), "right"),
                        Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key"))
                )
            ), DataRow::class)

        result shouldBe emptyList()
    }

    @Test
    fun `GenericSimulatorConnection can simulate LEFT JOIN`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("key" to  41, "entity" to "x"),
                    DataRow("key" to  42, "entity" to "y"),
                ), "left"),
                joins = listOf(
                    LeftJoin(
                        QuerySource(ConstantData(DummyEngine.orm,
                            DataRow("key" to 42, "attribute" to "foo"),
                            DataRow("key" to 43, "attribute" to "bar"),
                        ), "right"),
                        Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key"))
                )
            ), DataRow::class)

        result shouldContainExactly listOf(
            DataRow("left.key" to 41, "left.entity" to "x", "right.key" to null, "right.attribute" to null),
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to 42, "right.attribute" to "foo"),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate LEFT JOIN with empty right side`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("key" to  42, "entity" to "y"),
                ), "left"),
                joins = listOf(
                    LeftJoin(
                        QuerySource(ConstantData.empty(DummyEngine.orm, listOf("key", "attribute")), "right"),
                        Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key")))
            ), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to null, "right.attribute" to null),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate RIGHT JOIN`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("key" to  41, "entity" to "x"),
                    DataRow("key" to  42, "entity" to "y"),
                ), "left"),
                joins = listOf(
                    RightJoin(
                        QuerySource(ConstantData(DummyEngine.orm,
                            DataRow("key" to 42, "attribute" to "foo"),
                            DataRow("key" to 43, "attribute" to "bar"),
                        ), "right"),
                        Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key"))
                )
            ), DataRow::class)

        result shouldContainExactly listOf(
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to 42, "right.attribute" to "foo"),
            DataRow("left.key" to null, "left.entity" to null, "right.key" to 43, "right.attribute" to "bar"),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate RIGHT JOIN with empty left side`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData.empty(DummyEngine.orm, listOf("key", "entity")), "left"),
                joins = listOf(
                    RightJoin(
                        QuerySource(ConstantData(DummyEngine.orm,
                            DataRow("key" to 42, "attribute" to "foo"),
                        ), "right"),
                        Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key"))
                )
            ), DataRow::class)

        result.single().columnNames shouldBe listOf("left.key", "left.entity", "right.key", "right.attribute")
        result shouldContainExactly listOf(
            DataRow("left.key" to null, "left.entity" to null, "right.key" to 42, "right.attribute" to "foo"),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate CROSS JOIN`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("entity" to "x"),
                    DataRow("entity" to "y"),
                )),
                joins = listOf(
                    CrossJoin(
                        QuerySource(ConstantData(DummyEngine.orm,
                            DataRow("attribute" to "foo"),
                            DataRow("attribute" to "bar"),
                        )))
                )
            ), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "attribute" to "foo"),
            DataRow("entity" to "x", "attribute" to "bar"),
            DataRow("entity" to "y", "attribute" to "foo"),
            DataRow("entity" to "y", "attribute" to "bar"),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate correlated CROSS JOIN with Data expression`() {
        val unnest = mock<Expression<DummyEngine, Data<DummyEngine, Int>>> {
            whenever(it.defaultColumnName()).thenReturn("")
            whenever(it.subexpressions).thenReturn(listOf(Column<DummyEngine, Int>("values")))
        }
        connection.registerExpressionSimulator(mock<ExpressionSimulator<DummyEngine, Data<DummyEngine, *>, Expression<DummyEngine, Data<DummyEngine, *>>>> {
            whenever(it.expression).thenReturn(unnest::class)
            whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) { it.simulateExpression(eq(unnest)) })
                .thenReturn { row -> ConstantData(DummyEngine.orm, (row["values"] as kotlin.Array<*>).map { element -> DataRow("" to element) }) }
        })
        connection.correlatedJoinsEnabled = true

        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm,
                    DataRow("entity" to "x", "values" to arrayOf(1, 2)),
                    DataRow("entity" to "y", "values" to arrayOf(3, 4)),
                )),
                joins = listOf(
                    CrossJoin(QuerySource(DataExpressionData(unnest), "value"))
                ),
                columns = listOf(
                    Projection(Column<DummyEngine, String>("entity")),
                    Projection(Column<DummyEngine, Int>("value")),
                )
            ), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "value" to 1),
            DataRow("entity" to "x", "value" to 2),
            DataRow("entity" to "y", "value" to 3),
            DataRow("entity" to "y", "value" to 4),
        )
    }

    @Test
    fun `GenericSimulatorConnection can simulate SELECT with filter to empty result`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow("id" to 42))),
                filter = Constant(false),
            ), DataRow::class)

        result shouldBe emptyList()
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
    fun `GenericSimulatorConnection can simulate the AND-operator`() {
        val result = connection.execute(
            Select(
                source = QuerySource(ConstantData(DummyEngine.orm, DataRow())),
                columns = listOf(Projection(And(Constant(true), Constant(false)), "and")),
            ), DataRow::class
        )

        result.single()["and"] shouldBe false
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

    @Test
    fun `GenericSimulatorConnection can fetch Data by evaluating an expression`() {
        val data = ConstantData(DummyEngine.orm, DataRow("string" to "foo"), DataRow("string" to "bar"), DataRow("string" to "baz"))
        val dataExpression = mock<Expression<DummyEngine, Data<DummyEngine, DataRow>>> {
            whenever(it.defaultColumnName()).thenReturn("string")
        }
        val dataExpressionSimulator = mock<ExpressionSimulator<DummyEngine, Data<DummyEngine, *>, Expression<DummyEngine, Data<DummyEngine, *>>>> {
            whenever(it.expression).thenReturn(dataExpression::class)
            whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) { it.simulateExpression(eq(dataExpression)) })
                .thenReturn { data }
        }

        connection.registerExpressionSimulator(dataExpressionSimulator)
        val result = connection.execute(Select(QuerySource(DataExpressionData(dataExpression))), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(DataRow("string" to "foo"), DataRow("string" to "bar"), DataRow("string" to "baz"))
    }

    @Test
    fun `GenericSimulatorConnection can fetch Data by evaluating an expression that resolves to Data of primitives`() {
        val data = ConstantData(DummyEngine.orm, "foo", "bar", "baz")
        val dataExpression = mock<Expression<DummyEngine, Data<DummyEngine, DataRow>>> {
            whenever(it.defaultColumnName()).thenReturn("")
        }
        val dataExpressionSimulator = mock<ExpressionSimulator<DummyEngine, Data<DummyEngine, *>, Expression<DummyEngine, Data<DummyEngine, *>>>> {
            whenever(it.expression).thenReturn(dataExpression::class)
            whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) { it.simulateExpression(eq(dataExpression)) })
                .thenReturn { data }
        }

        connection.registerExpressionSimulator(dataExpressionSimulator)
        val result = connection.execute(Select(QuerySource(DataExpressionData(dataExpression), "string")), DataRow::class)

        result shouldContainExactlyInAnyOrder listOf(DataRow("string" to "foo"), DataRow("string" to "bar"), DataRow("string" to "baz"))
    }
}
