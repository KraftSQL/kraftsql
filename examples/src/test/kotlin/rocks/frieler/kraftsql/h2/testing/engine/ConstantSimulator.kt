package rocks.frieler.kraftsql.h2.testing.engine

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.expressions.Constant
import rocks.frieler.kraftsql.testing.engine.ConstantSimulator
import kotlin.reflect.KClass

class ConstantSimulator<T> : ConstantSimulator<H2Engine, T>() {
    @Suppress("UNCHECKED_CAST")
    override val expression = Constant::class as KClass<Constant<H2Engine, T>>
}
