package ru.pelmegov.eventmanager.core.ratelimit.strategy;

import com.sun.istack.internal.NotNull;
import lombok.extern.java.Log;

import java.util.concurrent.TimeUnit;

import static ru.pelmegov.eventmanager.constants.BucketConstants.MAX_BUCKET_SIZE;
import static ru.pelmegov.eventmanager.constants.BucketConstants.MIN_BUCKET_SIZE;

@Log
public abstract class AbstractTokenBucketStrategy implements RateLimitStrategy {

    protected final long bucketTokenCapacity;
    protected final long refillInterval;

    public long tokens = 0;
    protected long nextRefillTime = 0;

    protected AbstractTokenBucketStrategy(
            final long bucketTokenCapacity,
            final long refillInterval,
            @NotNull final TimeUnit refillIntervalTimeUnit) {
        checkPreconditions(bucketTokenCapacity, refillInterval);

        this.bucketTokenCapacity = bucketTokenCapacity;
        this.refillInterval = refillIntervalTimeUnit.toNanos(refillInterval);
    }

    private void checkPreconditions(final long bucketTokenCapacity, final long refillInterval) {
        if (refillInterval < 0) {
            log.severe("Bucket refill interval must not be negative");
            throw new IllegalArgumentException("Bucket refill interval must not be negative");
        }
        if (bucketTokenCapacity < MIN_BUCKET_SIZE) {
            log.severe("Bucket token capacity must not be smaller than MIN_BUCKET_SIZE: " + MIN_BUCKET_SIZE);
            throw new IllegalArgumentException("Bucket token capacity must not be smaller than MIN_BUCKET_SIZE: " + MIN_BUCKET_SIZE);
        }
        if (bucketTokenCapacity > MAX_BUCKET_SIZE) {
            log.severe("Bucket token capacity must be smaller than MAX_BUCKET_SIZE: " + MAX_BUCKET_SIZE);
            throw new IllegalArgumentException("Bucket token capacity must be smaller than MAX_BUCKET_SIZE: " + MAX_BUCKET_SIZE);
        }
    }

    @Override
    public boolean isThrottled() {
        return isThrottled(1);
    }

    @Override
    public boolean isThrottled(final long n) {
        synchronized (this) {
            if (getCurrentTokenCount() < n) {
                return true;
            }
            tokens -= n;
        }
        return false;
    }

    public long getCurrentTokenCount() {
        updateTokens();
        return tokens;
    }

    @Override
    public long timeToRelease(final long n, @NotNull final TimeUnit timeUnit) {
        if (getCurrentTokenCount() >= n) {
            return 0L;
        } else {
            long timeToIntervalEnd = nextRefillTime - System.nanoTime();
            if (timeToIntervalEnd < 0) {
                return timeToRelease(n, timeUnit);
            } else {
                return timeUnit.convert(timeToIntervalEnd, TimeUnit.NANOSECONDS);
            }
        }
    }

    public abstract void updateTokens();

}