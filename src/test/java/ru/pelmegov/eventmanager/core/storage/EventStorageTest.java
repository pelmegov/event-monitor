package ru.pelmegov.eventmanager.core.storage;

import org.junit.Before;
import org.junit.Test;
import ru.pelmegov.eventmanager.domain.EventData;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EventStorageTest {

    private EventStorage storage;

    @Before
    public void setUp() {
        storage = new EventStorage();
    }

    @Test
    public void addEvent() {
        storage.addEvent(new EventData("eventName", LocalDateTime.now()));
        storage.addEvent(new EventData("eventName", LocalDateTime.now()));
        storage.addEvent(new EventData("eventName", LocalDateTime.now()));
        storage.addEvent(new EventData("eventName", LocalDateTime.now().minusMinutes(2)));
        storage.addEvent(new EventData("eventName", LocalDateTime.now().minusMinutes(1)));
        storage.addEvent(new EventData("eventName", LocalDateTime.now().minusMinutes(2)));
    }

    @Test
    public void getEventsByTime() {
        final LocalDateTime nowTime = LocalDateTime.now();

        EventData testObject = new EventData("eventName", nowTime);
        storage.addEvent(testObject);

        storage.addEvent(new EventData("incorrectEvent", nowTime.minusMinutes(1)));
        storage.addEvent(new EventData("incorrectEvent", nowTime.plusMinutes(1)));

        assertThat(storage.getEventsAfterTime(nowTime), hasSize(1));
    }

    @Test
    public void getEventsBeforeOneMinute() {
        final LocalDateTime nowTime = LocalDateTime.now();

        storage.addEvent(new EventData("eventName", nowTime.minusMinutes(1)));
        storage.addEvent(new EventData("eventName", nowTime.minusMinutes(2)));
        storage.addEvent(new EventData("eventName", nowTime.minusMinutes(3)));
        storage.addEvent(new EventData("eventName", nowTime.minusMinutes(4)));
        storage.addEvent(new EventData("eventName", nowTime.minusMinutes(5)));

        storage.addEvent(new EventData("eventName", nowTime.minusMinutes(6)));

        assertThat(storage.getEventsAfterTime(nowTime.minusMinutes(5)), hasSize(5));
    }

}