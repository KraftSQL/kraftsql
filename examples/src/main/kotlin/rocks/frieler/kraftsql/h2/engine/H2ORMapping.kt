package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.JdbcORMapping
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.engine.ensuredPrimaryConstructor
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.Row
import rocks.frieler.kraftsql.objects.DataRow
import java.math.BigDecimal
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
        when {
            type == typeOf<String>() -> Types.CHARACTER_VARYING()
            type == typeOf<Boolean>() -> Types.BOOLEAN
            type == typeOf<Byte>() -> Types.TINYINT
            type == typeOf<Short>() -> Types.SMALLINT
            type == typeOf<Int>() -> Types.INTEGER
            type == typeOf<Long>() -> Types.BIGINT
            type == typeOf<Float>() -> Types.REAL
            type == typeOf<Double>() -> Types.DOUBLE_PRECISION
            type == typeOf<BigDecimal>() -> Types.NUMERIC(100, 50)
            type == typeOf<Instant>() -> Types.TIMESTAMP_WITH_TIME_ZONE
            type.jvmErasure.starProjectedType == typeOf<Array<*>>() -> Types.ARRAY(getTypeFor(type.arguments.single().type ?: Any::class.starProjectedType))
            type.jvmErasure.isData -> Types.ROW(type.jvmErasure.ensuredPrimaryConstructor().parameters.associate { param -> param.name!! to getTypeFor(param.type) })
            else -> throw NotImplementedError("Unsupported Kotlin type $type.")
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
            Types.NUMERIC -> typeOf<BigDecimal>()
            Types.TIMESTAMP_WITH_TIME_ZONE -> typeOf<Instant>()
            is Types.ARRAY -> Array::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, getKTypeFor(sqlType.contentType))))
            is Types.ROW -> typeOf<DataRow>()
            else -> throw NotImplementedError("Unsupported SQL type $sqlType")
        }

    override fun <T : Any> serialize(value: T?): Expression<H2Engine, T> {
        fun <T : Any> replaceWithH2Expressions(expression: Expression<H2Engine, T>) : Expression<H2Engine, T> =
            when (expression) {
                is rocks.frieler.kraftsql.expressions.Constant -> Constant(expression.value)
                is rocks.frieler.kraftsql.expressions.Row -> Row(
                    expression.values?.mapValues { (_, value) ->
                        @Suppress("UNCHECKED_CAST")
                        replaceWithH2Expressions(value as Expression<H2Engine, Any>)
                    })
                else -> expression
            }

        return replaceWithH2Expressions(super.serialize(value))
    }
}
