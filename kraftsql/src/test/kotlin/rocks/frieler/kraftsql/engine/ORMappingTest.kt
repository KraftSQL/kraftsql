package rocks.frieler.kraftsql.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import rocks.frieler.kraftsql.expressions.Row
import rocks.frieler.kraftsql.objects.Column
import rocks.frieler.kraftsql.objects.DataRow

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

    @Test
    fun `serialize serializes a DataRow into a Row expression`() {
        val dataRow = DataRow("foo" to "bar", "answer" to 42)

        val expression = TestableDummyEngine.orm.serialize(dataRow)

        expression should beInstanceOf<Row<TestableDummyEngine, *>>()
        (expression as Row).apply {
            values shouldNotBeNull {
                shouldContainKey("foo")
                shouldContainKey("answer")
            }
        }
    }
}
