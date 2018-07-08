package ru.pelmegov.eventmanager;

import ru.pelmegov.eventmanager.core.EventMonitor;
import ru.pelmegov.eventmanager.core.EventMonitorApplication;
import ru.pelmegov.eventmanager.domain.EventData;
import ru.pelmegov.eventmanager.core.StandardEventMonitor;

import java.time.LocalDateTime;
import java.util.List;

public class DefaultEventMonitorApplication implements EventMonitorApplication {

    private final EventMonitor eventMonitor;

    public DefaultEventMonitorApplication() {
        this.eventMonitor = StandardEventMonitor.createStandardEventMonitor();
    }

    @Override
    public List<EventData> lastMinuteEvents() {
        return eventMonitor.lastEventsAfterTime(LocalDateTime.now().minusMinutes(1));
    }

    @Override
    public List<EventData> lastHourEvents() {
        return eventMonitor.lastEventsAfterTime(LocalDateTime.now().minusHours(1));
    }

    @Override
    public List<EventData> lastDayEvents() {
        return eventMonitor.lastEventsAfterTime(LocalDateTime.now().minusDays(1));
    }

    @Override
    public boolean createNewEvent(String eventName, LocalDateTime eventTime) {
        return eventMonitor.putEvent(new EventData(eventName, eventTime));
    }

}
