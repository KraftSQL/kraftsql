package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.Type

class CHARACTER(private val length: Int = 1) : Type { override fun sql(): String = "CHARACTER($length)" }

@Suppress("ClassName")
class CHARACTER_VARYING(private val length: Int? = null) : Type { override fun sql() = "CHARACTER VARYING${ if (length != null) "(length: $length)" else ""}" }

val CHARACTER_LARGE_OBJECT = object : Type { override fun sql() = "CHARACTER LARGE OBJECT" }

val BOOLEAN = object : Type { override fun sql() = "BOOLEAN" }

val TINYINT = object : Type { override fun sql() = "TINYINT" }

val SMALLINT = object : Type { override fun sql() = "SMALLINT" }

val INTEGER = object : Type { override fun sql() = "INTEGER" }

val BIGINT = object : Type { override fun sql() = "BIGINT" }

val REAL = object : Type { override fun sql() = "REAL" }

val DOUBLE_PRECISION = object : Type { override fun sql() = "DOUBLE PRECISION" }
