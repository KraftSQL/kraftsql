package rocks.frieler.kraftsql.testing.engine

import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.engine.ORMapping
import rocks.frieler.kraftsql.engine.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object DummyEngine : Engine<DummyEngine> {
    object Types {
        val INTEGER = object : Type<DummyEngine, Int> {
            override fun naturalKType() = typeOf<Int>()
            override fun sql() = "INTEGER"
        }
        val TEXT = object : Type<DummyEngine, String> {
            override fun naturalKType() = typeOf<String>()
            override fun sql() = "TEXT"
        }
    }

    object orm : ORMapping<DummyEngine, Any> {
        override fun getTypeFor(type: KType): Type<DummyEngine, *> {
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
