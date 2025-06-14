package rate.limiter;

import java.time.temporal.ChronoUnit;

public class RateLimiterRules {

    private final int capacity;
    private final int period;
    private final ChronoUnit unit;

    private RateLimiterRules(int capacity, int period, ChronoUnit unit) {
        this.capacity = capacity;
        this.period = period;
        this.unit = unit;
    }

    static RateLimiterRules.Builder builder() {
        return new RateLimiterRules.Builder();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPeriod() {
        return period;
    }

    public ChronoUnit getUnit() {
        return unit;
    }

    static class Builder {
        private int maxAllowed;
        private int window;
        private ChronoUnit unit;

        Builder withCapacity(int capacity) {
            this.maxAllowed = capacity;
            return this;
        }

        Builder withPeriod(int period) {
            this.window = period;
            return this;
        }

        Builder withUnit(ChronoUnit unit) {
            this.unit = unit;
            return this;
        }

        RateLimiterRules build() {
            return new RateLimiterRules(this.maxAllowed, this.window, this.unit);
        }
    }
}
