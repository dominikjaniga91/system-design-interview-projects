package rate.limiter

import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import redis.clients.jedis.JedisPool
import spock.lang.Shared

@Testcontainers
class SlidingWindowRedisRateLimiterSpec extends RateLimiterBaseSpec {

    @Shared
    GenericContainer<GenericContainer> redis = new GenericContainer<>("redis:8.0.1")
            .withExposedPorts(6379)

    @Override
    void setupSpec() {
        limiter = new SlidingWindowRedisRateLimiter(new JedisPool(redis.getHost(), redis.getMappedPort(6379)),
                rateLimiterRules())
    }
}
