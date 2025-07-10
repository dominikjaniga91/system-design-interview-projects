package gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rate.limiter.RateLimiter;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.time.Instant;

import static spark.Spark.halt;

public class RateLimitFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimiter rateLimiter;

    public RateLimitFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void handle(Request request, Response response) {
        String service = request.headers("authenticated-service");
        LOGGER.info("Rate limiting request for service {}", service);
        boolean allowed = rateLimiter.register(service, Instant.now());
        if (!allowed) {
            LOGGER.warn("Rate limit exceeded for service {}", service);
            halt(429, "Rate limit exceeded");
        }
    }
}
