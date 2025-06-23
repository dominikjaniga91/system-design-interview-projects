package rate.limiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

class SlidingWindowRedisRateLimiter implements RateLimiter {

    private final ReentrantLock lock = new ReentrantLock();
    private final JedisPool jedisPool;
    private final RateLimiterRules rules;

    public SlidingWindowRedisRateLimiter(JedisPool jedisPool, RateLimiterRules rules) {
        this.jedisPool = jedisPool;
        this.rules = rules;
    }

    @Override
    public boolean register(String serviceName, Instant timestamp) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "rate_limiter:" + serviceName;
            lock.lock();
            if (jedis.hlen(key) < rules.maxAllowed()) {
                String fieldKey = timestamp.toString();
                Transaction transaction = jedis.multi();
                transaction.hset(key, fieldKey, "");
                transaction.hexpire(key, rules.period(), fieldKey);
                var result = transaction.exec();
                if (result.isEmpty()) {
                    throw new IllegalStateException("Redis transaction's result is empty");
                }
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
