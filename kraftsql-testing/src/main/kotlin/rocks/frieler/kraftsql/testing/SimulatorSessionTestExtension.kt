package rocks.frieler.kraftsql.testing

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactoryContext
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback
import rocks.frieler.kraftsql.engine.DefaultSession
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.testing.engine.SimulatorSession

open class SimulatorSessionTestExtension<E : Engine<E>>(
    val session: SimulatorSession<E>,
    private val defaultSessionToConfigure: DefaultSession<E>? = null
) : TestInstancePreConstructCallback, TestInstancePreDestroyCallback {

    override fun preConstructTestInstance(
        testInstanceFactoryContext: TestInstanceFactoryContext,
        extensionContext: ExtensionContext,
    ) {
        defaultSessionToConfigure?.set(SimulatorSession())
    }

    override fun preDestroyTestInstance(extensionContext: ExtensionContext) {
        defaultSessionToConfigure?.unset()
    }

    open class Builder<E : Engine<E>>(
        protected val session: SimulatorSession<E>,
    ) {
        protected var defaultSessionToConfigure: DefaultSession<E>? = null

        fun defaultSessionToConfigure(session: DefaultSession<E>) : Builder<E> {
            defaultSessionToConfigure = session
            return this
        }

        open fun build() : SimulatorSessionTestExtension<E> =
            SimulatorSessionTestExtension(session, defaultSessionToConfigure)
    }
}
