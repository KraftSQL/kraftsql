package rocks.frieler.kraftsql.engine

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object TestableDummyEngine : Engine<TestableDummyEngine> {
    object Types {
        val INTEGER = object : Type<TestableDummyEngine, Int> {
            override fun naturalKType() = typeOf<Int>()
            override fun sql() = "INTEGER"
        }
        val TEXT = object : Type<TestableDummyEngine, String> {
            override fun naturalKType() = typeOf<String>()
            override fun sql() = "TEXT"
        }
    }

    object orm : ORMapping<TestableDummyEngine, Any> {
        override fun getTypeFor(type: KType): Type<TestableDummyEngine, *> {
            return when (type) {
                in arrayOf(typeOf<Int>(), typeOf<Int?>()) -> Types.INTEGER
                in arrayOf(typeOf<String>(), typeOf<String?>()) -> Types.TEXT
                else -> throw IllegalArgumentException("unsupported type $type")
            }
        }

        override fun <T : Any> deserializeQueryResult(queryResult: Any, type: KClass<T>): List<T> =
            throw NotImplementedError("not needed for tests")
    }
}
