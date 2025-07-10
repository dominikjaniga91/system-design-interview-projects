package rate.limiter;

public enum RateLimiterStrategy {
    SLIDING_WINDOW,
    SLIDING_WINDOW_LUA_SCRIPT,
    SLIDING_WINDOW_REDIS
}
