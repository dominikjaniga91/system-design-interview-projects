package rate.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Instant;

class SlidingWindowLuaRateLimiter implements RateLimiter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SlidingWindowLuaRateLimiter.class);
    private final static String SCRIPT =
            """
            local key = KEYS[1]
            if redis.call('HLEN', key) >= tonumber(ARGV[3]) then
               return 0
            else
               redis.call('HSET', key, ARGV[1], '')
               redis.call('EXPIRE', key, ARGV[2])
               return 1
            end
            """;

    private final JedisPool jedisPool;
    private final RateLimiterRules rules;

    SlidingWindowLuaRateLimiter(JedisPool jedisPool, RateLimiterRules rules) {
        this.jedisPool = jedisPool;
        this.rules = rules;
    }

    @Override
    public boolean register(String serviceName, Instant timestamp) {
        LOGGER.info("Registering service {} at {}", serviceName, timestamp);
        String key = "rate_limiter:" + serviceName;
        String uniqueField = timestamp.toString();

        try (Jedis jedis = jedisPool.getResource()) {
            boolean allow = 1 == (Long) jedis.eval(SCRIPT,
                    1,
                    key,
                    uniqueField,
                    rules.periodAsString(),
                    rules.maxAllowedAsString()
            );
            if (allow) {
                LOGGER.info("Request from service {} has been accepted.", serviceName);
            } else {
                LOGGER.error("Request from service {} has been denied.", serviceName);
            }
            return allow;
        }
    }
}
