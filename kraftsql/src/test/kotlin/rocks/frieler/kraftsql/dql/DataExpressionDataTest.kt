package rocks.frieler.kraftsql.dql

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.expressions.Column
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.HasColumns

class DataExpressionDataTest {
    private val expression = mock<Expression<TestableDummyEngine, Data<TestableDummyEngine, DataRow>>>()
    private val dataExpressionData = DataExpressionData(expression)

    @Test
    fun `selectableColumnNames returns empty list when expression has no columns`() {
        dataExpressionData.selectableColumnNames.shouldBeEmpty()
    }

    @Test
    fun `selectableColumnNames returns selectable columns from expression if it has columns`() {
        val expressionWithColumns = mock<Expression<TestableDummyEngine, Data<TestableDummyEngine, DataRow>>>(
            extraInterfaces = arrayOf(HasColumns::class)
        ) { whenever((it as HasColumns<*, *>).selectableColumnNames).thenReturn(listOf("c1", "c2")) }

        DataExpressionData(expressionWithColumns).selectableColumnNames shouldBe listOf("c1", "c2")
    }

    @Test
    fun `sql() renders expression's SQL`() {
        whenever(expression.sql()).thenReturn("DATA()")

        dataExpressionData.sql() shouldBe "DATA()"
    }

    @Test
    fun `DataExpressionData provides maybe non-existent Column by name`() { // because column names are not reliable atm
        dataExpressionData["something"] shouldBe Column("something")
    }
}
