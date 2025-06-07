package rocks.frieler.kraftsql.h2.engine

import rocks.frieler.kraftsql.engine.Engine
import java.time.Instant
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

object H2Engine : Engine<H2Engine> {

    override fun getTypeFor(type: KType) =
        when (type) {
            String::class.starProjectedType -> CHARACTER_VARYING()
            Boolean::class.starProjectedType -> BOOLEAN
            Byte::class.starProjectedType -> TINYINT
            Short::class.starProjectedType -> SMALLINT
            Int::class.starProjectedType -> INTEGER
            Long::class.starProjectedType -> BIGINT
            Float::class.starProjectedType -> REAL
            Double::class.starProjectedType -> DOUBLE_PRECISION
            Instant::class.starProjectedType -> TIMESTAMP_WITH_TIME_ZONE
            else -> throw NotImplementedError("Unsupported Kotlin type $type")
        }
}
