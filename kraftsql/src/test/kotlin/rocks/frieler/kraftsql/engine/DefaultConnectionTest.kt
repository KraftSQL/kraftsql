package rocks.frieler.kraftsql.engine

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class DefaultConnectionTest {
    private val defaultConnection = object : DefaultConnection<TestableDummyEngine, Connection<TestableDummyEngine>>() {
        override fun instantiate(): Connection<TestableDummyEngine> = mock()
    }

    @Test
    fun `get() returns connection from instantiate()`() {
        defaultConnection.get() shouldNotBeNull {}
    }

    @Test
    fun `get() returns cached connection`() {
        val firstInstance = defaultConnection.get()
        val secondInstance = defaultConnection.get()

        secondInstance shouldBeSameInstanceAs firstInstance
    }

    @Test
    fun `set() allows to configure a custom connection`() {
        val customConnection = mock<Connection<TestableDummyEngine>>()

        defaultConnection.set(customConnection)

        defaultConnection.get() shouldBeSameInstanceAs customConnection
    }

    @Test
    fun `unset() clears cached connection`() {
        val firstInstance = defaultConnection.get()
        defaultConnection.unset()
        val secondInstance = defaultConnection.get()

        secondInstance shouldNotBeSameInstanceAs firstInstance
    }
}
