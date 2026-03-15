package rocks.frieler.kraftsql.testing.simulator.engine

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GenericEngineSimulatorTest {
    @Test
    fun `GenericEngineSimulator requires QueryEvaluator to use the same ExpressionEvaluator`() {
        shouldThrow<IllegalArgumentException> {
            GenericEngineSimulator<DummyEngine>(
                mock(),
                mock(),
                mock { whenever(it.expressionEvaluatorForChecking).thenReturn(mock()) },
            )
        }
    }
}
