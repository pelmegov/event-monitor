package ru.pelmegov.eventmanager.core.ratelimit;

import org.junit.Assert;
import org.junit.Test;
import ru.pelmegov.eventmanager.constants.EventCountConstants;
import ru.pelmegov.eventmanager.core.ratelimit.strategy.FixedRateLimitStrategy;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;

public class RateLimitTest {

    @Test
    public void twoExecuteForBucketSizeEqualsTwo() {
        RateLimit rateLimit = createRateLimit(2, 1);

        Assert.assertTrue(rateLimit.canProceed());
        Assert.assertTrue(rateLimit.canProceed());
    }

    @Test
    public void threeExecuteForBucketSizeEqualsTwo() {
        RateLimit rateLimit = createRateLimit(2, 1);

        rateLimit.canProceed();
        rateLimit.canProceed();

        Assert.assertFalse(rateLimit.canProceed());
    }

    @Test
    public void moreThanOneSecondsAccess() throws InterruptedException {
        RateLimit rateLimit = createRateLimit(10, 1);
        for (int i = 0; rateLimit.canProceed(); i++) {
            Thread.sleep(100);
            if (i > 10) {
                break;
            }
        }
    }

    @Test
    public void bucketSizeOverflow() {
        final int capacity = 10;
        final int interval = 1;
        RateLimit rateLimit = createRateLimit(capacity, interval);

        boolean result;
        int count = 0;
        while (result = rateLimit.canProceed()) {
            ++count;
            if (!result) {
                return;
            }
            if (count > capacity) {
                fail();
            }
        }
    }

    @Test(expected = RuntimeException.class)
    public void negativeInterval() {
        createRateLimit(10, -1);
    }

    @Test(expected = RuntimeException.class)
    public void bucketCapacityLessThanOne() {
        createRateLimit(0, 1);
    }

    @Test(expected = RuntimeException.class)
    public void bucketCapacityMoreThanMaximum() {
        createRateLimit(EventCountConstants.MAX_EVENTS_IN_DAY + 1, 1);
    }

    private RateLimit createRateLimit(final int capacity, final int interval) {
        return new RateLimit(
                new FixedRateLimitStrategy(capacity, interval, TimeUnit.SECONDS)
        );
    }
}