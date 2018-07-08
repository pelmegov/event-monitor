package ru.pelmegov.eventmanager.core.storage;

import ru.pelmegov.eventmanager.domain.EventData;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventStorage {

    private ConcurrentHashMap<LocalDateTime, List<EventData>> storage;

    public EventStorage() {
        this.storage = new ConcurrentHashMap<>();
    }

    public synchronized boolean addEvent(EventData event) {
        final LocalDateTime truncatedTime = truncateTimeToMinutes(event.getEventTime());

        List<EventData> queue = getOrCreateEventList(truncatedTime);
        queue.add(event);

        storage.put(truncatedTime, queue);
        return true;
    }

    public List<EventData> getEventsAfterTime(LocalDateTime eventTime) {
        LocalDateTime currentTime = LocalDateTime.now();

        LocalDateTime truncatedStartTime = truncateTimeToMinutes(eventTime);
        LocalDateTime truncatedEndTime = truncateTimeToMinutes(currentTime);

        List<EventData> events = getOrCreateEventList(truncatedEndTime);
        if (truncatedEndTime.equals(truncatedStartTime)) {
            return events;
        }

        events.addAll(getFirstListDataExcludeAfterSeconds(truncatedStartTime, currentTime));
        events.addAll(getAllEventsBetweenDates(truncatedStartTime.plusMinutes(1), truncatedEndTime));

        return events;
    }

    private List<EventData> getFirstListDataExcludeAfterSeconds(LocalDateTime truncatedPreviousTime, LocalDateTime currentTime) {
        List<EventData> firstEventList = getOrCreateEventList(truncatedPreviousTime);
        firstEventList.removeIf(e -> e.getEventTime().getSecond() < currentTime.getSecond());
        return firstEventList;
    }

    private List<EventData> getAllEventsBetweenDates(LocalDateTime truncatedPreviousTime, LocalDateTime truncatedCurrentTime) {
        List<EventData> events = new CopyOnWriteArrayList<>();
        while (!truncatedPreviousTime.equals(truncatedCurrentTime)) {
            events.addAll(getOrCreateEventList(truncatedPreviousTime));
            truncatedPreviousTime = truncatedPreviousTime.plusMinutes(1);
        }
        return events;
    }

    private LocalDateTime truncateTimeToMinutes(LocalDateTime eventTime) {
        return eventTime.truncatedTo(ChronoUnit.MINUTES);
    }

    private List<EventData> getOrCreateEventList(LocalDateTime truncatedTime) {
        List<EventData> events;
        if (storage.containsKey(truncatedTime)) {
            events = storage.get(truncatedTime);
        } else {
            events = new CopyOnWriteArrayList<>();
        }

        return events;
    }

    public void removeOldEvents() {
        LocalDateTime previousDayTime = truncateTimeToMinutes(LocalDateTime.now().minusDays(1).minusMinutes(1));

        while (storage.containsKey(previousDayTime)) {
            storage.remove(previousDayTime);
            previousDayTime = previousDayTime.minusMinutes(1);
        }
    }
}
