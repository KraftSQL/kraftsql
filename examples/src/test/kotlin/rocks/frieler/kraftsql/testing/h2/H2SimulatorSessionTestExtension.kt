package rocks.frieler.kraftsql.testing.h2

import org.junit.jupiter.api.extension.ExtendWith
import rocks.frieler.kraftsql.engine.DefaultSession
import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.h2.engine.H2InMemorySession
import rocks.frieler.kraftsql.testing.SimulatorSessionTestExtension
import rocks.frieler.kraftsql.testing.engine.SimulatorSession

class H2SimulatorSessionTestExtension(
    session : SimulatorSession<H2Engine> = SimulatorSession(),
    defaultSessionToConfigure: DefaultSession<H2Engine>? = H2InMemorySession.Default,
) : SimulatorSessionTestExtension<H2Engine>(session, defaultSessionToConfigure) {

    class Builder(
        session : SimulatorSession<H2Engine> = SimulatorSession(),
    ) : SimulatorSessionTestExtension.Builder<H2Engine>(session) {

        init {
            defaultSessionToConfigure(H2InMemorySession.Default)
        }

        override fun build(): H2SimulatorSessionTestExtension {
            return H2SimulatorSessionTestExtension(session, defaultSessionToConfigure)
        }
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(H2SimulatorSessionTestExtension::class)
annotation class WithH2SimulatorSession
