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

    @Test
    fun `KraftSQL supports non-nullable String`() {
        Type.get(typeOf<String>()) shouldBe Type.STRING
        Type.get(String::class, false) shouldBe Type.STRING
    }

    @Test
    fun `KraftSQL supports nullable String`() {
        Type.get(typeOf<String?>()) shouldBe Type.NULLABLE_STRING
        Type.get(String::class, true) shouldBe Type.NULLABLE_STRING
    }
}
