package ru.pelmegov.eventmanager.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import ru.pelmegov.eventmanager.constants.EventCountConstants;
import ru.pelmegov.eventmanager.core.ratelimit.RateLimit;
import ru.pelmegov.eventmanager.core.ratelimit.strategy.RateLimitStrategy;
import ru.pelmegov.eventmanager.domain.EventData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.stream.Collectors.toList;

@Log
public class EventMonitor {

    @Getter
    private EventArguments eventArguments;

    private BlockingQueue<EventData> eventDataBlockingQueue;
    private RateLimit rateLimitAccumulator;

    private EventMonitor() {
        this.eventDataBlockingQueue = new LinkedBlockingDeque<>(EventCountConstants.MAX_EVENTS_IN_DAY);
    }

    public boolean putEvent(@NonNull EventData eventData) {
        // check throttling
        if (!rateLimitAccumulator.canProceed()) {
            log.severe("This bucket is full");
            log.severe("Event " + eventData + " ignored.");
            return false;
        }

        removeOldEvents();

        if (checkAllowedEvents(eventData.getEventName())) {
            return eventDataBlockingQueue.add(eventData);
        }

        throw new IllegalArgumentException("Incorrect event!");
    }

    public List<EventData> lastEventsAfterTime(@NonNull LocalDateTime startTime) {
        return eventDataBlockingQueue.parallelStream()
                .filter(e -> e.getEventTime().isAfter(startTime))
                .collect(toList());
    }

    public static class Builder {
        EventMonitor eventMonitor = new EventMonitor();

        public Builder withArguments(EventArguments eventArguments) {
            eventMonitor.eventArguments = eventArguments;
            return this;
        }

        public Builder withRateLimitStrategy(RateLimitStrategy rateLimitStrategy) {
            eventMonitor.rateLimitAccumulator = new RateLimit(rateLimitStrategy);
            return this;
        }

        public EventMonitor build() {
            return eventMonitor;
        }
    }

    private void removeOldEvents() {
        eventDataBlockingQueue.removeIf(
                e -> e.getEventTime().isBefore(LocalDateTime.now().minusDays(1))
        );
    }

    private boolean checkAllowedEvents(@NonNull String eventName) {
        return eventArguments.getEventConstants().stream().anyMatch(eventName::equals);
    }
}
