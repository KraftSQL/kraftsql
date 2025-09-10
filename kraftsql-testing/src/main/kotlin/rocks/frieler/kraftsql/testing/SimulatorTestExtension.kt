package rocks.frieler.kraftsql.testing

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactoryContext
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.DefaultConnection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.testing.engine.SimulatorConnection

open class SimulatorTestExtension<E : Engine<E>, C : Connection<E>, S : SimulatorConnection<E>>(
    open val connectionProvider: () -> S,
    private val defaultConnectionToConfigure: DefaultConnection<E, C>? = null
) : TestInstancePreConstructCallback, TestInstancePreDestroyCallback {

    override fun preConstructTestInstance(
        testInstanceFactoryContext: TestInstanceFactoryContext,
        extensionContext: ExtensionContext,
    ) {
        @Suppress("UNCHECKED_CAST")
        // we cannot express that S must also implement C, so we have to let it fail at runtime :-(
        defaultConnectionToConfigure?.set(connectionProvider.invoke() as C)
    }

    override fun preDestroyTestInstance(extensionContext: ExtensionContext) {
        defaultConnectionToConfigure?.unset()
    }

    open class Builder<E : Engine<E>, C : Connection<E>, S : SimulatorConnection<E>>(
        protected open val connectionProvider: () -> S,
    ) {
        protected var defaultConnectionToConfigure: DefaultConnection<E, C>? = null

        fun defaultConnectionToConfigure(defaultConnection: DefaultConnection<E, C>) : Builder<E, C, S> {
            defaultConnectionToConfigure = defaultConnection
            return this
        }

        open fun build() : SimulatorTestExtension<E, C, S> =
            SimulatorTestExtension(connectionProvider, defaultConnectionToConfigure)
    }
}
