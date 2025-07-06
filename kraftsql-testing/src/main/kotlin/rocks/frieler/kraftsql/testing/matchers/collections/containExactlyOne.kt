package rocks.frieler.kraftsql.testing.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T> Iterable<T>.shouldContainExactlyOne(predicate: (T) -> Boolean): T {
    this should containExactlyOne(predicate)
    return single(predicate)
}

infix fun <T> Iterable<T>.containExactlyOne(predicate: (T) -> Boolean) = object : Matcher<Iterable<T>> {
    val matches = this@containExactlyOne.filter { predicate(it) }
    val count = matches.count()
    override fun test(value: Iterable<T>): MatcherResult {
        return MatcherResult(
            count == 1,
            {
                if (count > 1)
                    "There should be exactly one matching element, but there were $count, namely the following: $matches."
                else
                    "There should be exactly one matching element, but there was none in ${this@containExactlyOne}."
            },
            { "There was exactly one matching element, namely ${matches.single()}, but there should be either none or multiple." },
        )
    }
}
