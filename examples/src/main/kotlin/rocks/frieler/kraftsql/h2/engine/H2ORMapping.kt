package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.JdbcORMapping
import rocks.frieler.kraftsql.engine.Type
import rocks.frieler.kraftsql.engine.ensuredPrimaryConstructor
import rocks.frieler.kraftsql.expressions.Expression
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.h2.expressions.Row
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

object H2ORMapping : JdbcORMapping<H2Engine>(Types) {
    override fun getTypeFor(type: KType) : Type<H2Engine, *> =
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
            type == typeOf<LocalDate>() -> Types.DATE
            type.jvmErasure.starProjectedType == typeOf<Array<*>>() -> Types.ARRAY(getTypeFor(type.arguments.single().type ?: Any::class.starProjectedType))
            type.jvmErasure.isData -> Types.ROW(type.jvmErasure.ensuredPrimaryConstructor().parameters.associate { param -> param.name!! to getTypeFor(param.type) })
            else -> throw NotImplementedError("Unsupported Kotlin type $type.")
        }

    override fun <T : Any> serialize(value: T?): Expression<H2Engine, out T?> {
        fun <T : Any> replaceWithH2Expressions(expression: Expression<H2Engine, out T?>) : Expression<H2Engine, out T?> =
            when (expression) {
                is rocks.frieler.kraftsql.expressions.Constant -> Constant(expression.value)
                is rocks.frieler.kraftsql.expressions.Row -> Row(
                    expression.values?.mapValues { (_, value) ->
                        @Suppress("UNCHECKED_CAST")
                        replaceWithH2Expressions(value as Expression<H2Engine, Any?>)
                    })
                else -> expression
            }

        return replaceWithH2Expressions<T>(super.serialize(value))
    }
}
