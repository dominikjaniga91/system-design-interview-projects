package rate.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

class SlidingWindowRedisRateLimiter implements RateLimiter {
    private final static Logger LOGGER = LoggerFactory.getLogger(SlidingWindowRedisRateLimiter.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final JedisPool jedisPool;
    private final RateLimiterRules rules;

    public SlidingWindowRedisRateLimiter(JedisPool jedisPool, RateLimiterRules rules) {
        this.jedisPool = jedisPool;
        this.rules = rules;
    }

    @Override
    public boolean register(String serviceName, Instant timestamp) {
        LOGGER.info("Registering service {} at {}", serviceName, timestamp);
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "rate_limiter:" + serviceName;
            lock.lock();
            long numberOfRequests = jedis.hlen(key);
            if (numberOfRequests < rules.maxAllowed()) {
                String fieldKey = timestamp.toString();
                Transaction transaction = jedis.multi();
                transaction.hset(key, fieldKey, "");
                transaction.hexpire(key, rules.period(), fieldKey);
                var result = transaction.exec();
                if (result.isEmpty()) {
                    throw new IllegalStateException("Redis transaction's result is empty");
                }
                LOGGER.info("Request from service {} has been accepted. Number of requests: {}", serviceName, numberOfRequests + 1);
                return true;
            }
            LOGGER.error("Request from service {} has been denied. Number of requests: {}", serviceName, numberOfRequests);
            return false;
        } finally {
            lock.unlock();
        }
    }
}
