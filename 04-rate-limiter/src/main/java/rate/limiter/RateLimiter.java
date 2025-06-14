package rate.limiter;

import java.time.Instant;

public interface RateLimiter {

    boolean register(String serviceName, Instant timestamp);
}
