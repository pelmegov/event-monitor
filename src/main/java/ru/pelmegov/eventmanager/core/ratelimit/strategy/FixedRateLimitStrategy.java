package ru.pelmegov.eventmanager.core.ratelimit.strategy;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.TimeUnit;

public class FixedRateLimitStrategy extends AbstractTokenBucketStrategy {

    public FixedRateLimitStrategy(
            final long bucketTokenCapacity,
            final long refillInterval,
            @NotNull final TimeUnit refillIntervalTimeUnit) {
        super(bucketTokenCapacity, refillInterval, refillIntervalTimeUnit);
    }

    @Override
    public void updateTokens() {
        final long currentTime = System.nanoTime();
        if (currentTime < nextRefillTime) {
            return;
        }

        tokens = bucketTokenCapacity;
        nextRefillTime = currentTime + refillInterval;
    }
}