package rocks.frieler.kraftsql.h2.testing

import org.junit.jupiter.api.extension.ExtendWith
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.DefaultConnection
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.testing.SimulatorTestExtension
import rocks.frieler.kraftsql.testing.engine.GenericSimulatorConnection
import rocks.frieler.kraftsql.testing.engine.SimulatorConnection

class H2SimulatorTestExtension(
    connectionProvider : () -> SimulatorConnection<H2Engine> = { GenericSimulatorConnection() },
    defaultConnectionToConfigure: DefaultConnection<H2Engine, Connection<H2Engine>>? = H2Engine.DefaultConnection,
) : SimulatorTestExtension<H2Engine, Connection<H2Engine>, SimulatorConnection<H2Engine>>(connectionProvider, defaultConnectionToConfigure) {

    class Builder(
        connectionProvider : () -> SimulatorConnection<H2Engine> = { GenericSimulatorConnection() },
    ) : SimulatorTestExtension.Builder<H2Engine, Connection<H2Engine>, SimulatorConnection<H2Engine>>(connectionProvider) {

        init {
            defaultConnectionToConfigure(H2Engine.DefaultConnection)
        }

        override fun build(): H2SimulatorTestExtension {
            return H2SimulatorTestExtension(this@Builder.connectionProvider, defaultConnectionToConfigure)
        }
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(H2SimulatorTestExtension::class)
annotation class WithH2Simulator
