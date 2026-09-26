package rocks.frieler.kraftsql.engine

import rocks.frieler.kraftsql.data.Data
import java.util.ServiceLoader
import kotlin.reflect.KClass

/**
 * An engine (usually a SQL engine) to work with data inside it.
 */
interface Engine {
    /**
     * Collects [Data] as Kotlin objects of the specified [KClass], either [rocks.frieler.kraftsql.data.DataRow] or a
     * data class.
     *
     * @param data the [Data] to collect
     * @param type the Kotlin type of the data
     * @return the data as a list of Kotlin objects
     */
    fun <T : Any> collect(data: Data<T>, type: KClass<T>): List<T>

    companion object {
        /**
         * A default [Engine] loaded by the [ServiceLoader].
         *
         * Works only, if there is exactly one service implementation registered for the [Engine] interface.
         */
        val default: Engine
            get() = _default ?: loadDefaultEngine()

        private var _default: Engine? = null

        private fun loadDefaultEngine(): Engine {
            val candidates = ServiceLoader.load(Engine::class.java).toList()
            check(candidates.isNotEmpty()) { "No Engine implementation registered for ServiceLoader." }
            check(candidates.size < 2) { "Multiple Engine implementations registered for ServiceLoader: $candidates." }
            return candidates.single().also { _default = it }
        }

        /**
         * Resets the cached [default] [Engine], so it is loaded anew via the [ServiceLoader] on next access.
         */
        fun resetDefault() {
            _default = null
        }

        /**
         * Sets the [default] [Engine] explicitly to bypass the [ServiceLoader].
         *
         * Use this when there is more than one [Engine] registered, but you still want to define one as default.
         *
         * @param engine the desired [default] [Engine]
         */
        fun setDefault(engine: Engine) {
            _default = engine
        }
    }
}

/**
 * Collects the [Data] as Kotlin objects of the reified type, either [rocks.frieler.kraftsql.data.DataRow] or a data
 * class, using the [default Engine][Engine.default].
 *
 * @return the data as a list of Kotlin objects
 */
inline fun <reified T : Any> Data<T>.collect() = Engine.default.collect(this, T::class)
