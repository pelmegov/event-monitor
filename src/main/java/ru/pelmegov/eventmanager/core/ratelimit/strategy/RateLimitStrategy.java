package ru.pelmegov.eventmanager.core.ratelimit.strategy;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.TimeUnit;

public interface RateLimitStrategy {

    boolean isThrottled();

    boolean isThrottled(final long n);

    long timeToRelease(final long n, @NotNull final TimeUnit timeUnit);

}
