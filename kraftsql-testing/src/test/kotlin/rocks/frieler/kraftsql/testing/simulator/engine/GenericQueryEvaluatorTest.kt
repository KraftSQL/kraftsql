package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.dql.CrossJoin
import rocks.frieler.kraftsql.dql.DataExpressionData
import rocks.frieler.kraftsql.dql.InnerJoin
import rocks.frieler.kraftsql.dql.LeftJoin
import rocks.frieler.kraftsql.dql.Projection
import rocks.frieler.kraftsql.dql.QuerySource
import rocks.frieler.kraftsql.dql.RightJoin
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.expressions.`=`
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Constant
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.expressions.Min
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import rocks.frieler.kraftsql.testing.simulator.expressions.ExpressionSimulator
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator
import rocks.frieler.kraftsql.testing.simulator.expressions.SubexpressionCollector

class GenericQueryEvaluatorTest {
    private val queryEvaluator = GenericQueryEvaluator<DummyEngine>()

    private val state = mock<EngineState<DummyEngine>>()

    @Test
    fun `GenericQueryEvaluator can simulate SELECT from primitive ConstantData`() {
        val result = Select<DummyEngine, DataRow>(source = QuerySource(ConstantData(DummyEngine.orm, 1, 2, 3)))
            .let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(DataRow("" to 1), DataRow("" to 2), DataRow("" to 3))
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT from source with alias`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("answer" to 42L)), "data"),
            columns = listOf(Projection(Column<DummyEngine, Long>("data.answer"))),
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result.single()["data.answer"] shouldBe 42L
    }

    @Test
    fun `Empty column names of a QuerySource are just named by the alias`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("" to 42L)), "answer"),
            columns = listOf(Projection(Column<DummyEngine, Long>("answer"))),
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result.single()["answer"] shouldBe 42L
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT from table`() {
        val table = Table<DummyEngine, DataRow>(
            "unit-tests",
            "test-data",
            "table",
            listOf(rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT))
        )
        whenever(state.getTable(table.qualifiedName)).thenReturn(table to mutableListOf(DataRow("c" to "foo")))

        val result = Select<DummyEngine, DataRow>(source = QuerySource(table))
            .let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(DataRow("c" to "foo"))
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT from empty table`() {
        val table = Table<DummyEngine, DataRow>(
            "unit-tests",
            "test-data",
            "table",
            listOf(rocks.frieler.kraftsql.objects.Column("c", DummyEngine.Types.TEXT))
        )
        whenever(state.getTable(table.qualifiedName)).thenReturn(table to emptyList<DataRow>().toMutableList())

        val result = Select<DummyEngine, DataRow>(source = QuerySource(table))
            .let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldBe emptyList()
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT from sub-query`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(Select(source = QuerySource(ConstantData(DummyEngine.orm, DataRow("c" to "foo")))))
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(DataRow("c" to "foo"))
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT from empty sub-query`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(Select(source = QuerySource(ConstantData.empty(DummyEngine.orm, listOf("c")))))
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldBe emptyList()
    }

    @Test
    fun `GenericQueryEvaluator can fetch Data by evaluating an expression`() {
        val data = ConstantData(DummyEngine.orm, DataRow("string" to "foo"), DataRow("string" to "bar"), DataRow("string" to "baz"))
        val dataExpression = mock<Expression<DummyEngine, Data<DummyEngine, DataRow>>> {
            whenever(it.defaultColumnName()).thenReturn("string")
        }
        val dataExpressionSimulator = mock<ExpressionSimulator<DummyEngine, Data<DummyEngine, *>, Expression<DummyEngine, Data<DummyEngine, *>>>> {
            whenever(it.expression).thenReturn(dataExpression::class)
            whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) { it.simulateExpression(eq(dataExpression)) })
                .thenReturn { data }
        }
        val queryEvaluatorWithDataExpressionSimulator = GenericQueryEvaluator(
            expressionEvaluator = GenericExpressionEvaluator<DummyEngine>().apply {
                registerExpressionSimulator(dataExpressionSimulator)
            }
        )

        val result = Select<DummyEngine, DataRow>(QuerySource(DataExpressionData(dataExpression)))
            .let { context(state) { queryEvaluatorWithDataExpressionSimulator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(DataRow("string" to "foo"), DataRow("string" to "bar"), DataRow("string" to "baz"))
    }

    @Test
    fun `GenericQueryEvaluator can fetch Data by evaluating an expression that resolves to Data of primitives`() {
        val data = ConstantData(DummyEngine.orm, "foo", "bar", "baz")
        val dataExpression = mock<Expression<DummyEngine, Data<DummyEngine, DataRow>>> {
            whenever(it.defaultColumnName()).thenReturn("")
        }
        val dataExpressionSimulator = mock<ExpressionSimulator<DummyEngine, Data<DummyEngine, *>, Expression<DummyEngine, Data<DummyEngine, *>>>> {
            whenever(it.expression).thenReturn(dataExpression::class)
            whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) { it.simulateExpression(eq(dataExpression)) })
                .thenReturn { data }
        }
        val queryEvaluatorWithDataExpressionSimulator = GenericQueryEvaluator(
            expressionEvaluator = GenericExpressionEvaluator<DummyEngine>().apply {
                registerExpressionSimulator(dataExpressionSimulator)
            }
        )

        val result = Select<DummyEngine, DataRow>(QuerySource(DataExpressionData(dataExpression), "string"))
            .let { context(state) { queryEvaluatorWithDataExpressionSimulator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(DataRow("string" to "foo"), DataRow("string" to "bar"), DataRow("string" to "baz"))
    }

    @Test
    fun `GenericQueryEvaluator can simulate INNER JOIN`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 41, "entity" to "x"),
            DataRow("key" to 42, "entity" to "y"),
        )
        val rightSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 42, "attribute" to "foo"),
            DataRow("key" to 43, "attribute" to "bar"),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide, "left"),
            joins = listOf(
                InnerJoin(
                    QuerySource(rightSide, "right"),
                    Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactly listOf(
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to 42, "right.attribute" to "foo"),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate correlated INNER JOIN with SELECT statement`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("entity" to "x", "value" to 1),
            DataRow("entity" to "y", "value" to 2),
        )
        val rightSide = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("addition" to "x"))),
            columns = listOf(
                Projection(Column<DummyEngine, String>("addition")),
                Projection(Column<DummyEngine, Int>("value"), "value_again"),
            ))

        queryEvaluator.correlatedJoinsEnabled = true
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide),
            joins = listOf(
                InnerJoin(
                    QuerySource(rightSide),
                    Column<DummyEngine, String>("entity") `=` Column<DummyEngine, String>("addition")),
            ),
            columns = listOf(
                Projection(Column<DummyEngine, String>("entity")),
                Projection(Column<DummyEngine, Int>("value")),
                Projection(Column<DummyEngine, Int?>("value_again")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "value" to 1, "value_again" to 1),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate LEFT JOIN`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 41, "entity" to "x"),
            DataRow("key" to 42, "entity" to "y"),
        )
        val rightSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 42, "attribute" to "foo"),
            DataRow("key" to 43, "attribute" to "bar"),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide, "left"),
            joins = listOf(
                LeftJoin(
                    QuerySource(rightSide, "right"),
                    Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactly listOf(
            DataRow("left.key" to 41, "left.entity" to "x", "right.key" to null, "right.attribute" to null),
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to 42, "right.attribute" to "foo"),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate correlated LEFT JOIN with SELECT statement`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("entity" to "x", "value" to 1),
            DataRow("entity" to "y", "value" to 2),
        )
        val rightSide = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("addition" to "x"))),
            columns = listOf(
                Projection(Column<DummyEngine, String>("addition")),
                Projection(Column<DummyEngine, Int>("value"), "value_again"),
            ))

        queryEvaluator.correlatedJoinsEnabled = true
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide),
            joins = listOf(
                LeftJoin(
                    QuerySource(rightSide),
                    Column<DummyEngine, String>("entity") `=` Column<DummyEngine, String>("addition")),
            ),
            columns = listOf(
                Projection(Column<DummyEngine, String>("entity")),
                Projection(Column<DummyEngine, Int>("value")),
                Projection(Column<DummyEngine, Int?>("value_again")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "value" to 1, "value_again" to 1),
            DataRow("entity" to "y", "value" to 2, "value_again" to null),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate RIGHT JOIN`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 41, "entity" to "x"),
            DataRow("key" to 42, "entity" to "y"),
        )
        val rightSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 42, "attribute" to "foo"),
            DataRow("key" to 43, "attribute" to "bar"),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide, "left"),
            joins = listOf(
                RightJoin(
                    QuerySource(rightSide, "right"),
                    Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactly listOf(
            DataRow("left.key" to 42, "left.entity" to "y", "right.key" to 42, "right.attribute" to "foo"),
            DataRow("left.key" to null, "left.entity" to null, "right.key" to 43, "right.attribute" to "bar"),
        )
    }


    @Test
    fun `GenericQueryEvaluator can simulate CROSS JOIN`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("entity" to "x"),
            DataRow("entity" to "y"),
        )
        val rightSide = ConstantData(DummyEngine.orm,
            DataRow("attribute" to "foo"),
            DataRow("attribute" to "bar"),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide),
            joins = listOf(
                CrossJoin(QuerySource(rightSide)),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "attribute" to "foo"),
            DataRow("entity" to "x", "attribute" to "bar"),
            DataRow("entity" to "y", "attribute" to "foo"),
            DataRow("entity" to "y", "attribute" to "bar"),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate correlated CROSS JOIN with SELECT statement`() {
        queryEvaluator.correlatedJoinsEnabled = true
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("entity" to "x", "value" to 1),
            DataRow("entity" to "y", "value" to 2),
        )
        val rightSide = Select<DummyEngine, DataRow>(
            source = QuerySource(
                ConstantData(DummyEngine.orm,
                    DataRow("addition" to "a"),
                    DataRow("addition" to "b")
                )),
            columns = listOf(
                Projection(Column<DummyEngine, String>("addition")),
                Projection(Column<DummyEngine, Int>("value"), "value_again"),
            ),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide),
            joins = listOf(
                CrossJoin(QuerySource(rightSide))
            ),
            columns = listOf(
                Projection(Column<DummyEngine, String>("entity")),
                Projection(Column<DummyEngine, Int>("value")),
                Projection(Column<DummyEngine, String>("addition")),
                Projection(Column<DummyEngine, Int>("value_again")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "value" to 1, "addition" to "a", "value_again" to 1),
            DataRow("entity" to "x", "value" to 1, "addition" to "b", "value_again" to 1),
            DataRow("entity" to "y", "value" to 2, "addition" to "a", "value_again" to 2),
            DataRow("entity" to "y", "value" to 2, "addition" to "b", "value_again" to 2),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate correlated CROSS JOIN with Data expression`() {
        val unnest = mock<Expression<DummyEngine, Data<DummyEngine, Int>>> {
            whenever(it.defaultColumnName()).thenReturn("")
        }
        val queryEvaluatorPreparedForUnnestInCorrelatedJoin = GenericQueryEvaluator(
            subexpressionCollector = mock<SubexpressionCollector<DummyEngine>> {
                whenever(it.collectAllSubexpressions(unnest)).thenReturn(listOf(Column<DummyEngine, Int>("values")))
            },
            expressionEvaluator = GenericExpressionEvaluator<DummyEngine>().apply {
                registerExpressionSimulator(mock<ExpressionSimulator<DummyEngine, Data<DummyEngine, *>, Expression<DummyEngine, Data<DummyEngine, *>>>> {
                    whenever(it.expression).thenReturn(unnest::class)
                    whenever(context(any<ExpressionSimulator.SubexpressionCallbacks<DummyEngine>>()) { it.simulateExpression(eq(unnest)) })
                        .thenReturn { row -> ConstantData(DummyEngine.orm, (row["values"] as Array<*>).map { element -> DataRow("" to element) }) }
                })
            },
        ).apply {
            correlatedJoinsEnabled = true
        }

        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("entity" to "x", "values" to arrayOf(1, 2)),
            DataRow("entity" to "y", "values" to arrayOf(3, 4)),
        )
        val rightSide = DataExpressionData(unnest)

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide),
            joins = listOf(
                CrossJoin(QuerySource(rightSide, "value")),
            ),
            columns = listOf(
                Projection(Column<DummyEngine, String>("entity")),
                Projection(Column<DummyEngine, Int>("value")),
            )
        ).let { context(state) { queryEvaluatorPreparedForUnnestInCorrelatedJoin.selectRows(it) } }

        result shouldContainExactlyInAnyOrder listOf(
            DataRow("entity" to "x", "value" to 1),
            DataRow("entity" to "x", "value" to 2),
            DataRow("entity" to "y", "value" to 3),
            DataRow("entity" to "y", "value" to 4),
        )
    }

    @Test
    fun `GenericQueryEvaluator can simulate INNER JOIN with empty result`() {
        val leftSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 41, "entity" to "x"),
        )
        val rightSide = ConstantData(DummyEngine.orm,
            DataRow("key" to 43, "attribute" to "bar"),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(leftSide, "left"),
            joins = listOf(
                InnerJoin(
                    QuerySource(rightSide, "right"),
                    Column<DummyEngine, Int>("left.key") `=` Column<DummyEngine, Int>("right.key")),
            )
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldBe emptyList()
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT of certain columns of empty data`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData.empty(DummyEngine.orm, listOf("c1", "c2"))),
            columns = listOf(Projection(Column<DummyEngine, String>("c1")))
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldBe emptyList()
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT of all columns`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("integer" to 42, "string" to "foo"))),
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result.single()["integer"] shouldBe 42
        result.single()["string"] shouldBe "foo"
    }

    @Test
    fun `GenericQueryEvaluator selects columns from source and joins when selecting all columns`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("left" to "foo"))),
            joins = listOf(
                InnerJoin(QuerySource(ConstantData(DummyEngine.orm, DataRow("right" to "bar"))), Constant(true)))
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result.single().columnNames shouldContainExactly listOf("left", "right")
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT of all columns when grouping`() {
        val dummyData = ConstantData(
            DummyEngine.orm,
            DataRow("integer" to 42, "string" to "foo"),
            DataRow("integer" to 43, "string" to "bar"),
            DataRow("integer" to 43, "string" to "baz"),
        )

        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(dummyData),
            grouping = listOf(dummyData["integer"]),
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldHaveSize 2
        result.forEach { row ->
            row.columnNames shouldBe listOf("integer")
        }
    }



    @Test
    fun `GenericQueryEvaluator can simulate SELECT of a single row by aggregating over all data without grouping`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("value" to 1), DataRow("value" to 2))),
            columns = listOf(Projection(Min(Column<DummyEngine, Int>("value"))))
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldHaveSize 1
    }

    @Test
    fun `GenericQueryEvaluator can simulate SELECT with filter to empty result`() {
        val result = Select<DummyEngine, DataRow>(
            source = QuerySource(ConstantData(DummyEngine.orm, DataRow("id" to 42))),
            filter = Constant(false),
        ).let { context(state) { queryEvaluator.selectRows(it) } }

        result shouldBe emptyList()
    }
}
