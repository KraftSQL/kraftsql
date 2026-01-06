package rocks.frieler.kraftsql.engine

import java.math.BigDecimal
import java.sql.DriverManager
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object TestableJdbcEngine : JdbcEngine<TestableJdbcEngine>() {

    @Suppress("ClassName")
    object types : Types<TestableJdbcEngine> {

        val INTEGER = object : Type<TestableJdbcEngine, Int> {
            override fun sql() = "INTEGER"
            override fun naturalKType(): KType = typeOf<Int>()
        }

        class NUMERIC(val precision: Int, val scale: Int) : Type<TestableJdbcEngine, BigDecimal> {
            override fun sql() = "NUMERIC($precision, $scale)"

            override fun naturalKType(): KType = typeOf<BigDecimal>()

            companion object {
                val matcher = "^NUMERIC(\\((\\d+), ?(\\d+)\\))?$".toRegex()

                fun parse(numericType: String) : NUMERIC {
                    val match = matcher.matchEntire(numericType)!!
                    return NUMERIC(
                        match.groupValues[2].let { if (it.isNotEmpty()) it.toInt() else 100 },
                        match.groupValues[3].let { if (it.isNotEmpty()) it.toInt() else 50 })
                }
            }
        }

        override fun parseType(type: String): Type<TestableJdbcEngine, *> = when {
            type == INTEGER.sql() -> INTEGER
            type.matches(NUMERIC.matcher) -> NUMERIC.parse(type)
            else -> error("unknown type: '$type'")
        }
    }

    val orm = object : JdbcORMapping<TestableJdbcEngine>(types) {
        override fun getTypeFor(type: KType): Type<TestableJdbcEngine, *> {
            TODO("Not yet implemented")
        }
    }

    fun openConnection(): java.sql.Connection = DriverManager.getConnection("jdbc:h2:mem:testing;DATABASE_TO_UPPER=FALSE")
}
