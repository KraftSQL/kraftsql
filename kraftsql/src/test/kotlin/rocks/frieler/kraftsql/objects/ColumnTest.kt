package rocks.frieler.kraftsql.objects

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.engine.TestableDummyEngine
import rocks.frieler.kraftsql.engine.Type

class ColumnTest {
    private val someSQLType = mock<Type<TestableDummyEngine, Any>> { whenever(it.sql()).thenReturn("TYPE") }

    @Test
    fun `Column's SQL defines Column as name and type`() {
        val columnDefinition = Column("col", someSQLType)

        columnDefinition.sql() shouldBe "col ${someSQLType.sql()}"
    }

    @Test
    fun `Column's SQL definition appends NOT NULL constraint if not nullable`() {
        val columnDefinition = Column("col", someSQLType, nullable = false)

        columnDefinition.sql() shouldEndWith " NOT NULL"
    }
}
