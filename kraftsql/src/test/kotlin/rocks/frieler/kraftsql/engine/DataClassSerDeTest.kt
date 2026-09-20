package rocks.frieler.kraftsql.engine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import rocks.frieler.kraftsql.data.Column
import rocks.frieler.kraftsql.data.DataRow
import rocks.frieler.kraftsql.data.Type

class DataClassSerDeTest {

    @Test
    fun `getSchemaFor returns a column for each primary constructor parameter`() {
        data class Something(val x: Int, val y: Int)

        val schema = DataClassSerDe.getSchemaFor(Something::class)

        schema shouldContainExactlyInAnyOrder listOf(Column("x", Type.INTEGER), Column("y", Type.INTEGER))
    }

    @Test
    fun `getSchemaFor throws an exception if the given class is not a data class`() {
        shouldThrow<IllegalArgumentException> {
            DataClassSerDe.getSchemaFor(Any::class)
        }
    }

    @Test
    fun `serialize returns a DataRow with the item's properties in constructor-parameter order`() {
        data class Something(val x: Int, val y: Int)

        val row = DataClassSerDe.serialize(Something(1, 2))

        row shouldBe DataRow("x" to 1, "y" to 2)
    }

    @Test
    fun `serialize throws an exception if the given item's class is not a data class`() {
        shouldThrow<IllegalArgumentException> {
            DataClassSerDe.serialize(Any())
        }
    }

    @Test
    fun `deserialize builds an instance of the given type from the DataRow's fields`() {
        data class Something(val x: Int, val y: Int)

        val something = DataClassSerDe.deserialize(DataRow("x" to 1, "y" to 2), Something::class)

        something shouldBe Something(1, 2)
    }

    @Test
    fun `deserialize throws an exception if the given type is not a data class`() {
        shouldThrow<IllegalArgumentException> {
            DataClassSerDe.deserialize(DataRow("foo" to "bar"), Any::class)
        }
    }

    @Test
    fun `deserialize throws an exception if the DataRow is missing a field for a constructor parameter`() {
        data class Something(val x: Int, val y: Int)

        shouldThrow<IllegalStateException> {
            DataClassSerDe.deserialize(DataRow("x" to 1), Something::class)
        }
    }

    @Test
    fun `deserialize ignores additional fields`() {
        data class Something(val x: Int, val y: Int)

        val something = DataClassSerDe.deserialize(DataRow("x" to 1, "y" to 2, "z" to 3), Something::class)

        something shouldBe Something(1, 2)

    }
}
