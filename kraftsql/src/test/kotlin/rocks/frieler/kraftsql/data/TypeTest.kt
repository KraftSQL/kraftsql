package rocks.frieler.kraftsql.data

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.reflect.typeOf

class TypeTest {
    @Test
    fun `KraftSQL support non-nullable Int`() {
        Type.get(typeOf<Int>()) shouldBe Type.INTEGER
        Type.get(Int::class, false) shouldBe Type.INTEGER
    }

    @Test
    fun `KraftSQL support nullable Int`() {
        Type.get(typeOf<Int?>()) shouldBe Type.NULLABLE_INTEGER
        Type.get(Int::class, true) shouldBe Type.NULLABLE_INTEGER
    }
}
