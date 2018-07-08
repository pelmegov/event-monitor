package ru.pelmegov.eventmanager.core;

import ru.pelmegov.eventmanager.domain.EventData;

import java.time.LocalDateTime;
import java.util.List;

public interface EventMonitorApplication {

    List<EventData> lastMinuteEvents();

    List<EventData> lastHourEvents();

    List<EventData> lastDayEvents();

    boolean createNewEvent(String eventName, LocalDateTime eventTime);

}
