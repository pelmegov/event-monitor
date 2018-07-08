package ru.pelmegov.eventmanager.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import ru.pelmegov.eventmanager.core.ratelimit.RateLimit;
import ru.pelmegov.eventmanager.core.ratelimit.strategy.RateLimitStrategy;
import ru.pelmegov.eventmanager.core.storage.EventStorage;
import ru.pelmegov.eventmanager.domain.EventData;

import java.time.LocalDateTime;
import java.util.List;

@Log
public class EventMonitor {

    @Getter
    private EventArguments eventArguments;

    private EventStorage eventDataStorage;
    private RateLimit rateLimitAccumulator;

    private EventMonitor() {
        this.eventDataStorage = new EventStorage();
    }

    public boolean putEvent(@NonNull EventData eventData) {
        // check throttling
        if (!rateLimitAccumulator.canProceed()) {
            log.severe("This bucket is full");
            log.severe("Event " + eventData + " ignored.");
            return false;
        }

        eventDataStorage.removeOldEvents();

        if (checkAllowedEvents(eventData.getEventName())) {
            return eventDataStorage.addEvent(eventData);
        }

        throw new IllegalArgumentException("Incorrect event!");
    }

    public List<EventData> lastEventsAfterTime(@NonNull LocalDateTime startTime) {
        return eventDataStorage.getEventsAfterTime(startTime);
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

    private boolean checkAllowedEvents(@NonNull String eventName) {
        return eventArguments.getEventConstants().stream().anyMatch(eventName::equals);
    }
}
