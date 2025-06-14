package rate.limiter

import spock.lang.Specification
import spock.util.time.MutableClock

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class RateLimiterSpec extends Specification {

    private RateLimiter limiter
    private Clock clock

    void setup() {
        def time = Instant.parse("2025-06-13T12:00:00Z")
        clock = Clock.fixed(time, ZoneOffset.UTC)
        limiter = rateLimiter(this.clock)
    }

    def "should allow request when it does not exceed the limit"() {
        given:
        def timestamp = this.clock.instant().minusMillis(100)
        def serviceName = "service-1"

        when:
        def response = limiter.register(serviceName, timestamp)

        then:
        response
    }

    def "should deny fourth service request when limit has been reached"() {
        given:
        def serviceName = "service-1"
        limiter.register(serviceName, this.clock.instant().minusMillis(500))
        limiter.register(serviceName, this.clock.instant().minusMillis(400))
        limiter.register(serviceName, this.clock.instant().minusMillis(300))

        when:
        def response = limiter.register(serviceName, this.clock.instant().minusMillis(100))

        then:
        !response
    }

    def "should allow request after window time has passed"() {
        given:
        def clock = new MutableClock()
        def instant = clock.instant()
        def timestamp = instant.minusMillis(100)
        def serviceName = "service-1"
        def rateLimiter = rateLimiter(clock)
        rateLimiter.register(serviceName, instant.minusMillis(800))
        rateLimiter.register(serviceName, instant.minusMillis(700))
        rateLimiter.register(serviceName, instant.minusMillis(600))

        when:
        def fourth = rateLimiter.register(serviceName, timestamp)
        clock.adjust { t -> t.plus(300, ChronoUnit.MILLIS) }
        def fifth = rateLimiter.register(serviceName, timestamp)

        then:
        !fourth
        fifth
    }

    def "should deny each fourth service request when recording requests in parallel"() {
        given:
        def pool = Executors.newFixedThreadPool(5)
        def tasks = (1..5).collect { i ->
            {
                limiter.register("service-$i", this.clock.instant().minusMillis(500))
                limiter.register("service-$i", this.clock.instant().minusMillis(400))
                limiter.register("service-$i", this.clock.instant().minusMillis(300))
                return limiter.register("service-$i", this.clock.instant().minusMillis(100))
            } as Callable<Boolean>
        }

        when:
        def futures = pool.invokeAll(tasks)
        def results = futures.collect { it.get() }

        then:
        results.count { !it } == 5
        pool.shutdown()
    }

    private InMemoryRateLimiter rateLimiter(Clock clock) {
        new InMemoryRateLimiter(RateLimiterRules.builder()
                .withCapacity(3)
                .withPeriod(1)
                .withUnit(ChronoUnit.SECONDS)
                .build(), clock)
    }
}
