package gateway;

import okhttp3.OkHttpClient;
import rate.limiter.RateLimiterFactory;
import rate.limiter.RateLimiterRules;
import rate.limiter.RateLimiterStrategy;
import redis.clients.jedis.JedisPool;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

class AppDependencies {

    private final RateLimitFilter rateLimitFilter;
    private final UrlShortener urlShortener;

    AppDependencies(AppConfig config) {

        var okClient = okHttpClient();
        this.urlShortener = new UrlShortener(config.getUrlsShortenerUri(), okClient);
        var jedisPool = new JedisPool(config.getJedisHost(), config.getJedisPort());

        var rules = RateLimiterRules.builder().withMaxAllowed(2).withPeriod(10).withUnit(ChronoUnit.SECONDS).build();
        var rateLimiter = new RateLimiterFactory(Clock.systemDefaultZone(), jedisPool, rules).createRateLimiter(RateLimiterStrategy.SLIDING_WINDOW);
        this.rateLimitFilter = new RateLimitFilter(rateLimiter);
    }

    private OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }

    RateLimitFilter getRateLimitFilter() {
        return rateLimitFilter;
    }

    UrlShortener getUrlShortener() {
        return urlShortener;
    }
}
