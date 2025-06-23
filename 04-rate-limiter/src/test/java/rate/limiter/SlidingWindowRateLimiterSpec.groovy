package rate.limiter

class SlidingWindowRateLimiterSpec extends RateLimiterBaseSpec {

    @Override
    void setupSpec() {
        limiter = new SlidingWindowRateLimiter(rateLimiterRules(), clock)
    }
}
