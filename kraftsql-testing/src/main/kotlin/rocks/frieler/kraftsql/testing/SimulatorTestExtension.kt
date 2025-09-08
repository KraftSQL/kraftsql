package rocks.frieler.kraftsql.testing

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactoryContext
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback
import rocks.frieler.kraftsql.engine.DefaultConnection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.testing.engine.SimulatorConnection

open class SimulatorTestExtension<E : Engine<E>>(
    open val connection: SimulatorConnection<E>,
    private val defaultConnectionToConfigure: DefaultConnection<E>? = null
) : TestInstancePreConstructCallback, TestInstancePreDestroyCallback {

    override fun preConstructTestInstance(
        testInstanceFactoryContext: TestInstanceFactoryContext,
        extensionContext: ExtensionContext,
    ) {
        defaultConnectionToConfigure?.set(connection)
    }

    override fun preDestroyTestInstance(extensionContext: ExtensionContext) {
        defaultConnectionToConfigure?.unset()
    }

    open class Builder<E : Engine<E>>(
        protected open val connection: SimulatorConnection<E>,
    ) {
        protected var defaultConnectionToConfigure: DefaultConnection<E>? = null

        fun defaultConnectionToConfigure(defaultConnection: DefaultConnection<E>) : Builder<E> {
            defaultConnectionToConfigure = defaultConnection
            return this
        }

        open fun build() : SimulatorTestExtension<E> =
            SimulatorTestExtension(connection, defaultConnectionToConfigure)
    }
}
