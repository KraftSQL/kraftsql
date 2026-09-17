package rocks.frieler.kraftsql.data

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A data type in KraftSQL's abstraction of data.
 *
 * KraftSQL allows a set Kotlin types for which the individual connectors implement the mapping to native types in their
 * SQL engine.
 */
interface Type {
    override fun toString(): String

    companion object {
        // TODO: Implement allowed types.

        /**
         * A non-nullable 32bit Integer.
         */
        val INTEGER = object : Type { override fun toString() = "Int" }

        /**
         * A nullable 32bit Integer.
         */
        val NULLABLE_INTEGER = object : Type { override fun toString() = "Int?" }

        /**
         * Returns the [Type] corresponding to the given [KType].
         *
         * @param kType a Kotlin type
         * @return the corresponding KraftSQL [Type]
         */
        fun get(kType: KType) = when (kType) {
            typeOf<Int>() -> INTEGER
            typeOf<Int?>() -> NULLABLE_INTEGER
            else -> throw IllegalStateException("Unsupported type $kType")
        }

        /**
         * Returns the [Type] corresponding to the given [KClass] and nullability modifier.
         *
         * @param kClass a Kotlin class
         * @param nullable whether `null` is allowed or not
         * @return the corresponding KraftSQL [Type]
         */
        fun get(kClass: KClass<*>, nullable: Boolean = false) = when (kClass) {
            Int::class -> if (nullable) NULLABLE_INTEGER else INTEGER
            else -> throw IllegalStateException("Unsupported type $kClass")
        }
    }
}
