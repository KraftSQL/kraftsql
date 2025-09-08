package rocks.frieler.kraftsql.h2.testing

import org.junit.jupiter.api.extension.ExtendWith
import rocks.frieler.kraftsql.engine.DefaultConnection
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemoryConnection
import rocks.frieler.kraftsql.testing.SimulatorTestExtension
import rocks.frieler.kraftsql.testing.engine.SimulatorConnection

class H2SimulatorTestExtension(
    connectionProvider : () -> SimulatorConnection<H2Engine> = { SimulatorConnection() },
    defaultConnectionToConfigure: DefaultConnection<H2Engine>? = H2InMemoryConnection.Default,
) : SimulatorTestExtension<H2Engine>(connectionProvider, defaultConnectionToConfigure) {

    class Builder(
        connectionProvider : () -> SimulatorConnection<H2Engine> = { SimulatorConnection() },
    ) : SimulatorTestExtension.Builder<H2Engine>(connectionProvider) {

        init {
            defaultConnectionToConfigure(H2InMemoryConnection.Default)
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
