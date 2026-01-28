package rocks.frieler.kraftsql.dql

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
    fun `columnNames returns the expressions default column name`() {
        whenever(expression.defaultColumnName()).thenReturn("DATA()")

        dataExpressionData.columnNames shouldBe listOf(expression.defaultColumnName())
    }

    @Test
    fun `columnNames returns columns from expression if it has columns`() {
        val expressionWithColumns = mock<Expression<TestableDummyEngine, Data<TestableDummyEngine, DataRow>>>(
            extraInterfaces = arrayOf(HasColumns::class)
        ) { whenever((it as HasColumns<*, *>).columnNames).thenReturn(listOf("c1", "c2")) }

        DataExpressionData(expressionWithColumns).columnNames shouldBe listOf("c1", "c2")
    }

    @Test
    fun `sql() renders expression's SQL`() {
        whenever(expression.sql()).thenReturn("DATA()")

        dataExpressionData.sql() shouldBe "DATA()"
    }

    @Test
    fun `DataExpressionData provides maybe non-existent Column by name`() { // because column names are not reliable atm
        whenever(expression.defaultColumnName()).thenReturn("")

        dataExpressionData["something"] shouldBe Column("something")
    }
}
