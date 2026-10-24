package rocks.frieler.kraftsql.testing

import org.junit.jupiter.api.extension.ExtendWith
import rocks.frieler.kraftsql.local.engine.KraftSqlLocalEngine

/**
 * [DefaultEngineExtension] that sets a fresh [KraftSqlLocalEngine] as default.
 */
class KraftSqlLocalEngineExtension : DefaultEngineExtension({ KraftSqlLocalEngine() })

/**
 * Activates the [KraftSqlLocalEngineExtension] for the annotated test class, so it runs against a fresh
 * [KraftSqlLocalEngine] as [default Engine][rocks.frieler.kraftsql.engine.Engine.default].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(KraftSqlLocalEngineExtension::class)
annotation class WithKraftSqlLocalEngine
