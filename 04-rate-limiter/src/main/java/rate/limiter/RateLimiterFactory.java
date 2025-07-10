package rate.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.time.Clock;

public class RateLimiterFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(RateLimiterFactory.class);

    private final Clock clock;
    private final JedisPool jedisPool;
    private final RateLimiterRules rules;

    public RateLimiterFactory(Clock clock, JedisPool jedisPool, RateLimiterRules rules) {
        this.clock = clock;
        this.jedisPool = jedisPool;
        this.rules = rules;
    }

    public RateLimiter createRateLimiter(RateLimiterStrategy strategy) {
        LOGGER.info("Creating {} with rules {}", strategy, rules);
        return switch (strategy) {
            case SLIDING_WINDOW -> new SlidingWindowRateLimiter(rules, clock);
            case SLIDING_WINDOW_LUA_SCRIPT -> new SlidingWindowLuaRateLimiter(jedisPool, rules);
            case SLIDING_WINDOW_REDIS -> new SlidingWindowRedisRateLimiter(jedisPool, rules);
        };
    }
}
