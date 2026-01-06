package rocks.frieler.kraftsql.engine

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal

class JdbcORMappingTest {
    @Test
    fun `deserializeQueryResult can deserialize BigDecimal into DataRow`() {
        val rows = TestableJdbcEngine.openConnection().use { connection ->
            val queryResult = connection.createStatement().executeQuery("SELECT 1.23 AS `number`")

            TestableJdbcEngine.orm.deserializeQueryResult(queryResult, DataRow::class)
        }

        rows.single()["number"] shouldBe BigDecimal.valueOf(123L, 2)
    }

    @Test
    fun `deserializeQueryResult can deserialize NULL into a data class field`() {
        data class SomethingWithNullableField(val number: Int?)

        val rows = TestableJdbcEngine.openConnection().use { connection ->
            val queryResult = connection.createStatement().executeQuery("SELECT CAST(NULL AS INTEGER) AS `number`")

            TestableJdbcEngine.orm.deserializeQueryResult(queryResult, SomethingWithNullableField::class)
        }

        rows.single().number shouldBe null

    }
}
