package rocks.frieler.kraftsql.testing.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldContainAny(predicate: (T) -> Boolean): T {
    this should containAny(predicate)
    return first(predicate)
}

infix fun <T> Iterable<T>.shouldContainNone(predicate: (T) -> Boolean) {
    this shouldNot containAny(predicate)
}

infix fun <T> Iterable<T>.containAny(predicate: (T) -> Boolean) = object : Matcher<Iterable<T>> {
    override fun test(value: Iterable<T>): MatcherResult {
        val matches = value.filter(predicate)

        return MatcherResult(
            matches.isNotEmpty(),
            { "There should be at least one matching element, but there was none." },
            { "There should be no matching elements, but there were ${matches}." },
        )
    }
}
