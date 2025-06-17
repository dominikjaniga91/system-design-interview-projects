package rate.limiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Instant;

class SlidingWindowLuaRateLimiter implements RateLimiter {

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
        String key = "rate_limiter:" + serviceName;
        String uniqueField = timestamp.toString();

        try (Jedis jedis = jedisPool.getResource()) {
            return 1 == (Long) jedis.eval(SCRIPT,
                    1,
                    key,
                    uniqueField,
                    rules.periodAsString(),
                    rules.maxAllowedAsString()
            );
        }
    }
}
