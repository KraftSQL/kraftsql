package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.JdbcORMapping
import rocks.frieler.kraftsql.engine.Type
import java.time.Instant
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

object H2ORMapping : JdbcORMapping<H2Engine>(Types) {
    override fun getTypeFor(type: KType) : Type<H2Engine> =
        when (type.jvmErasure.starProjectedType) {
            String::class.starProjectedType -> Types.CHARACTER_VARYING()
            Boolean::class.starProjectedType -> Types.BOOLEAN
            Byte::class.starProjectedType -> Types.TINYINT
            Short::class.starProjectedType -> Types.SMALLINT
            Int::class.starProjectedType -> Types.INTEGER
            Long::class.starProjectedType -> Types.BIGINT
            Float::class.starProjectedType -> Types.REAL
            Double::class.starProjectedType -> Types.DOUBLE_PRECISION
            Instant::class.starProjectedType -> Types.TIMESTAMP_WITH_TIME_ZONE
            Array::class.starProjectedType -> Types.ARRAY(getTypeFor(type.arguments.single().type ?: Any::class.starProjectedType))
            else -> throw NotImplementedError("Unsupported Kotlin type $type")
        }

    override fun getKTypeFor(sqlType: Type<H2Engine>): KType =
        when (sqlType) {
            is Types.CHARACTER -> typeOf<String>()
            is Types.CHARACTER_VARYING -> typeOf<String>()
            Types.CHARACTER_LARGE_OBJECT -> typeOf<String>()
            Types.BOOLEAN -> typeOf<Boolean>()
            Types.TINYINT -> typeOf<Byte>()
            Types.SMALLINT -> typeOf<Short>()
            Types.INTEGER -> typeOf<Int>()
            Types.BIGINT -> typeOf<Long>()
            Types.REAL -> typeOf<Float>()
            Types.DOUBLE_PRECISION -> typeOf<Double>()
            Types.TIMESTAMP_WITH_TIME_ZONE -> typeOf<Instant>()
            is Types.ARRAY -> Array::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, getKTypeFor(sqlType.contentType))))
            else -> throw NotImplementedError("Unsupported SQL type $sqlType")
        }
}
