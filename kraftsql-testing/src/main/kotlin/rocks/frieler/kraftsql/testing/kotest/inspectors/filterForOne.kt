package rocks.frieler.kraftsql.testing.kotest.inspectors

import io.kotest.inspectors.filterMatching
import io.kotest.inspectors.forOne

inline fun <T, C : Collection<T>> C.filterForOne(fn: (T) -> Unit): T = forOne(fn).filterMatching(fn).single()
