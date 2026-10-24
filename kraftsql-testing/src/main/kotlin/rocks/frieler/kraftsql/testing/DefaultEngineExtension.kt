package rocks.frieler.kraftsql.testing

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactoryContext
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback
import rocks.frieler.kraftsql.engine.Engine

/**
 * JUnit extension that sets the [default Engine][Engine.default] for each test instance.
 *
 * A fresh [Engine] is provided before the test instance is constructed, so it is already the default during the
 * initialization of the test class. After the test instance is destroyed, the default is
 * [reset][Engine.resetDefault]. A default [Engine] that was set explicitly before is not restored.
 *
 * The [default Engine][Engine.default] is global state, so test classes using this extension must not run in parallel.
 *
 * @param engineProvider provides the [Engine] to use as default for a test instance
 */
open class DefaultEngineExtension(
    private val engineProvider: () -> Engine,
) : TestInstancePreConstructCallback, TestInstancePreDestroyCallback {

    override fun preConstructTestInstance(
        testInstanceFactoryContext: TestInstanceFactoryContext,
        extensionContext: ExtensionContext,
    ) {
        Engine.setDefault(engineProvider())
    }

    override fun preDestroyTestInstance(extensionContext: ExtensionContext) {
        Engine.resetDefault()
    }
}
