package rocks.frieler.kraftsql.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.objects.Column

class ORMappingTest {
    @Test
    fun `getSchemaFor rejects non data-class`() {
        shouldThrow<IllegalStateException> {
            TestableDummyEngine.orm.getSchemaFor(mock { whenever(it.isData).thenReturn(false) })
        }
    }

    @Test
    fun `getSchemaFor data-class returns Columns for primary constructor parameters`() {
        data class TestDataClass(val id: Int, val value: String?)

        val schema = TestableDummyEngine.orm.getSchemaFor(TestDataClass::class)

        schema shouldContainExactly listOf(
            Column("id", TestableDummyEngine.Types.INTEGER, false),
            Column("value", TestableDummyEngine.Types.TEXT, true),
        )
    }
}
