package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.engine.Types

object Types : Types<H2Engine> {
    class CHARACTER(private val length: Int = 1) : Type<H2Engine> { override fun sql(): String = "CHARACTER($length)" }

    @Suppress("ClassName")
    class CHARACTER_VARYING(private val length: Int? = null) : Type<H2Engine> { override fun sql() = "CHARACTER VARYING${ if (length != null) "($length)" else ""}" }

    val CHARACTER_LARGE_OBJECT = object : Type<H2Engine> { override fun sql() = "CHARACTER LARGE OBJECT" }

    val BOOLEAN = object : Type<H2Engine> { override fun sql() = "BOOLEAN" }

    val TINYINT = object : Type<H2Engine> { override fun sql() = "TINYINT" }

    val SMALLINT = object : Type<H2Engine> { override fun sql() = "SMALLINT" }

    val INTEGER = object : Type<H2Engine> { override fun sql() = "INTEGER" }

    val BIGINT = object : Type<H2Engine> { override fun sql() = "BIGINT" }

    val REAL = object : Type<H2Engine> { override fun sql() = "REAL" }

    val DOUBLE_PRECISION = object : Type<H2Engine> { override fun sql() = "DOUBLE PRECISION" }

    class NUMERIC(val precision: Int, val scale: Int) : Type<H2Engine> {
        override fun sql() = "NUMERIC($precision, $scale)"

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

    val TIMESTAMP_WITH_TIME_ZONE = object : Type<H2Engine> { override fun sql() = "TIMESTAMP WITH TIME ZONE" }

    class ARRAY(val contentType: Type<H2Engine>) : Type<H2Engine> { override fun sql() = "${contentType.sql()} ARRAY" }

    class ROW(val fields: Map<String, Type<H2Engine>>) : Type<H2Engine> {
        override fun sql() = "ROW(${fields.entries.joinToString(", ") { (name, type) -> "\"${name}\" ${type.sql()}" }})"
        
        companion object {
            val matcher = "^ROW\\(.+\\)$".toRegex()
        }
    }

    override fun parseType(type: String) : Type<H2Engine> = when {
        type.matches("^CHARACTER(\\(\\d+\\))?$".toRegex()) -> CHARACTER(if (type.contains('(')) type.substring(10, type.length - 1).toInt() else 1)
        type.matches("^CHARACTER VARYING(\\(\\d+\\))?$".toRegex()) -> CHARACTER_VARYING(if (type.contains('(')) type.substring(18, type.length - 1).toInt() else null)
        type == CHARACTER_LARGE_OBJECT.sql() -> CHARACTER_LARGE_OBJECT
        type == BOOLEAN.sql() -> BOOLEAN
        type == TINYINT.sql() -> TINYINT
        type == SMALLINT.sql() -> SMALLINT
        type == INTEGER.sql() -> INTEGER
        type == BIGINT.sql() -> BIGINT
        type == REAL.sql() -> REAL
        type == DOUBLE_PRECISION.sql() -> DOUBLE_PRECISION
        type.matches(NUMERIC.matcher) -> NUMERIC.parse(type)
        type == TIMESTAMP_WITH_TIME_ZONE.sql() -> TIMESTAMP_WITH_TIME_ZONE
        type.matches("^.+ ARRAY$".toRegex()) -> ARRAY(parseType(type.dropLast(6)))
        type.matches(ROW.matcher) -> ROW(type.removePrefix("ROW(").removeSuffix(")").split(",").associate {
            val match = "\"([^\"]+)\" ([^,]+)".toRegex().matchEntire(it.trim())!!
            match.groupValues[1] to parseType(match.groupValues[2])
        })
        else -> error("unknown h2 type: '$type'")
    }
}
