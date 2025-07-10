package rate.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

class SlidingWindowRateLimiter implements RateLimiter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SlidingWindowRateLimiter.class);
    private final Map<String, Queue<Instant>> bucket = new ConcurrentHashMap<>();

    private final RateLimiterRules rules;
    private final Clock clock;

    SlidingWindowRateLimiter(RateLimiterRules rules, Clock clock) {
        this.rules = rules;
        this.clock = clock;
    }

    @Override
    public boolean register(String serviceName, Instant timestamp) {
        LOGGER.info("Registering service {} at {}", serviceName, timestamp);
        Queue<Instant> timestamps = bucket.computeIfAbsent(serviceName, k -> new ConcurrentLinkedDeque<>());
        Instant window = Instant.now(clock).minus(rules.period(), rules.unit());

        while (!timestamps.isEmpty() && hasExpiredRequests(timestamps, window)) {
            timestamps.poll();
        }

        int requestsNumber = timestamps.size();
        if (requestsNumber < rules.maxAllowed()) {
            timestamps.offer(timestamp);
            LOGGER.info("Request from service {} has been accepted. Current number of requests {}", serviceName, requestsNumber + 1);
            return true;
        } else {
            LOGGER.error("Request from service {} has been denied. Current number of requests {}", serviceName, requestsNumber );
            return false;
        }
    }

    private boolean hasExpiredRequests(Queue<Instant> timestamps, Instant window) {
        Instant peek = timestamps.peek();
        return peek != null && peek.isBefore(window);
    }
}
