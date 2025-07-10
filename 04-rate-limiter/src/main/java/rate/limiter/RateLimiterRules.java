package rate.limiter;

import java.time.temporal.ChronoUnit;

public class RateLimiterRules {

    private final int maxAllowed;
    private final int period;
    private final ChronoUnit unit;

    private RateLimiterRules(int maxAllowed, int period, ChronoUnit unit) {
        this.maxAllowed = maxAllowed;
        this.period = period;
        this.unit = unit;
    }

    public static RateLimiterRules.Builder builder() {
        return new RateLimiterRules.Builder();
    }

    public int maxAllowed() {
        return maxAllowed;
    }

    public String maxAllowedAsString() {
        return String.valueOf(maxAllowed);
    }

    public int period() {
        return period;
    }

    public String periodAsString() {
        return String.valueOf(period);
    }

    public ChronoUnit unit() {
        return unit;
    }

    public static class Builder {
        private int maxAllowed;
        private int window;
        private ChronoUnit unit;

        public Builder withMaxAllowed(int maxAllowed) {
            this.maxAllowed = maxAllowed;
            return this;
        }

        public Builder withPeriod(int period) {
            this.window = period;
            return this;
        }

        public Builder withUnit(ChronoUnit unit) {
            this.unit = unit;
            return this;
        }

        public RateLimiterRules build() {
            return new RateLimiterRules(this.maxAllowed, this.window, this.unit);
        }
    }

    @Override
    public String toString() {
        return "RateLimiterRules{" +
                "maxAllowed=" + maxAllowed +
                ", period=" + period +
                ", unit=" + unit +
                '}';
    }
}
