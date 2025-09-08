package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.engine.Types
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

object Types : Types<H2Engine> {
    class CHARACTER(private val length: Int = 1) : Type<H2Engine, String> {
        override fun sql(): String = "CHARACTER($length)"
        override fun naturalKType(): KType = typeOf<String>()
    }

    @Suppress("ClassName")
    class CHARACTER_VARYING(private val length: Int? = null) : Type<H2Engine, String> {
        override fun sql() = "CHARACTER VARYING${ if (length != null) "($length)" else ""}"
        override fun naturalKType(): KType = typeOf<String>()
    }

    val CHARACTER_LARGE_OBJECT = object : Type<H2Engine, String> {
        override fun sql() = "CHARACTER LARGE OBJECT"
        override fun naturalKType(): KType = typeOf<String>()
    }

    val BOOLEAN = object : Type<H2Engine, Boolean> {
        override fun sql() = "BOOLEAN"
        override fun naturalKType(): KType = typeOf<Boolean>()
    }

    val TINYINT = object : Type<H2Engine, Byte> {
        override fun sql() = "TINYINT"
        override fun naturalKType(): KType = typeOf<Byte>()
    }

    val SMALLINT = object : Type<H2Engine, Short> {
        override fun sql() = "SMALLINT"
        override fun naturalKType(): KType = typeOf<Short>()
    }

    val INTEGER = object : Type<H2Engine, Int> {
        override fun sql() = "INTEGER"
        override fun naturalKType(): KType = typeOf<Int>()
    }

    val BIGINT = object : Type<H2Engine, Long> {
        override fun sql() = "BIGINT"
        override fun naturalKType(): KType = typeOf<Long>()
    }

    val REAL = object : Type<H2Engine, Float> {
        override fun sql() = "REAL"
        override fun naturalKType(): KType = typeOf<Float>()
    }

    val DOUBLE_PRECISION = object : Type<H2Engine, Double> {
        override fun sql() = "DOUBLE PRECISION"
        override fun naturalKType(): KType = typeOf<Double>()
    }

    class NUMERIC(val precision: Int, val scale: Int) : Type<H2Engine, BigDecimal> {
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

    val TIMESTAMP_WITH_TIME_ZONE = object : Type<H2Engine, Instant> {
        override fun sql() = "TIMESTAMP WITH TIME ZONE"
        override fun naturalKType(): KType = typeOf<Instant>()
    }

    val DATE = object : Type<H2Engine, LocalDate> {
        override fun sql() = "DATE"
        override fun naturalKType(): KType = typeOf<LocalDate>()
    }

    class ARRAY<C : Any> (val contentType: Type<H2Engine, C>) : Type<H2Engine, C> {
        override fun sql() = "${contentType.sql()} ARRAY"
        override fun naturalKType(): KType = Array::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, contentType.naturalKType())))
    }

    class ROW(val fields: Map<String, Type<H2Engine, *>>) : Type<H2Engine, DataRow> {
        override fun sql() = "ROW(${fields.entries.joinToString(", ") { (name, type) -> "\"${name}\" ${type.sql()}" }})"
        override fun naturalKType(): KType = typeOf<DataRow>()

        companion object {
            val matcher = "^ROW\\(.+\\)$".toRegex()
        }
    }

    override fun parseType(type: String) : Type<H2Engine, *> = when {
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
        type == DATE.sql() -> DATE
        type.matches("^.+ ARRAY$".toRegex()) -> ARRAY(parseType(type.dropLast(6)))
        type.matches(ROW.matcher) -> ROW(type.removePrefix("ROW(").removeSuffix(")").split(",").associate {
            val match = "\"([^\"]+)\" ([^,]+)".toRegex().matchEntire(it.trim())!!
            match.groupValues[1] to parseType(match.groupValues[2])
        })
        else -> error("unknown h2 type: '$type'")
    }
}
