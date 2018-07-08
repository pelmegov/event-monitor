package ru.pelmegov.eventmanager.core.ratelimit;

import ru.pelmegov.eventmanager.core.ratelimit.strategy.RateLimitStrategy;

public class RateLimit {

    private final RateLimitStrategy strategy;

    public RateLimit(RateLimitStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean canProceed() {
        return !strategy.isThrottled();
    }

}