package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.Type

class CHARACTER(private val length: Int = 1) : Type<H2Engine> { override fun sql(): String = "CHARACTER($length)" }

@Suppress("ClassName")
class CHARACTER_VARYING(private val length: Int? = null) : Type<H2Engine> { override fun sql() = "CHARACTER VARYING${ if (length != null) "(length: $length)" else ""}" }

val CHARACTER_LARGE_OBJECT = object : Type<H2Engine> { override fun sql() = "CHARACTER LARGE OBJECT" }

val BOOLEAN = object : Type<H2Engine> { override fun sql() = "BOOLEAN" }

val TINYINT = object : Type<H2Engine> { override fun sql() = "TINYINT" }

val SMALLINT = object : Type<H2Engine> { override fun sql() = "SMALLINT" }

val INTEGER = object : Type<H2Engine> { override fun sql() = "INTEGER" }

val BIGINT = object : Type<H2Engine> { override fun sql() = "BIGINT" }

val REAL = object : Type<H2Engine> { override fun sql() = "REAL" }

val DOUBLE_PRECISION = object : Type<H2Engine> { override fun sql() = "DOUBLE PRECISION" }

val TIMESTAMP_WITH_TIME_ZONE = object : Type<H2Engine> { override fun sql() = "TIMESTAMP WITH TIME ZONE" }
