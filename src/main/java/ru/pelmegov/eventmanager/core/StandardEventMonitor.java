package ru.pelmegov.eventmanager.core;

import lombok.Getter;
import ru.pelmegov.eventmanager.constants.EventCountConstants;
import ru.pelmegov.eventmanager.core.ratelimit.strategy.FixedRateLimitStrategy;
import ru.pelmegov.eventmanager.core.ratelimit.strategy.RateLimitStrategy;
import ru.pelmegov.eventmanager.constants.EventConstants;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class StandardEventMonitor {

    @Getter
    private RateLimitStrategy rateLimitStrategy;

    @Getter
    private EventArguments eventArguments;

    public StandardEventMonitor() {
        prepareDefaultRateLimitStrategy();
        prepareDefaultEventArguments();
    }

    public StandardEventMonitor(RateLimitStrategy rateLimitStrategy, EventArguments eventArguments) {
        this.rateLimitStrategy = rateLimitStrategy;
        this.eventArguments = eventArguments;
    }

    public static EventMonitor createStandardEventMonitor() {
        final StandardEventMonitor standardEventMonitor = new StandardEventMonitor();

        return new EventMonitor.Builder()
                .withArguments(standardEventMonitor.eventArguments)
                .withRateLimitStrategy(standardEventMonitor.rateLimitStrategy)
                .build();
    }

    private void prepareDefaultRateLimitStrategy() {
        this.rateLimitStrategy = new FixedRateLimitStrategy(
                EventCountConstants.MAX_EVENTS_IN_MINUTE, 1, TimeUnit.MINUTES);
    }

    private void prepareDefaultEventArguments() {
        this.eventArguments = new EventArguments();

        eventArguments.setEventConstants(
                Arrays.asList(
                        EventConstants.ADD_ITEM_TO_CART,
                        EventConstants.CHECKOUT,
                        EventConstants.PAYMENT,
                        EventConstants.REMOVE_ITEM_FROM_CART
                )
        );
    }

}
