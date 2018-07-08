package ru.pelmegov.eventmanager;

import org.junit.Before;
import org.junit.Test;
import ru.pelmegov.eventmanager.core.EventMonitorApplication;
import ru.pelmegov.eventmanager.domain.EventData;
import ru.pelmegov.eventmanager.constants.EventConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventMonitorApplicationTest {

    private EventMonitorApplication application;

    @Before
    public void before() {
        this.application = new DefaultEventMonitorApplication();
    }

    @Test
    public void putOneNewEventAndCheckResult() {
        final boolean eventResult = application.createNewEvent(EventConstants.ADD_ITEM_TO_CART, LocalDateTime.now());

        assertTrue(eventResult);
    }

    @Test
    public void lastMinuteEvents() {
        application.createNewEvent(EventConstants.ADD_ITEM_TO_CART, LocalDateTime.now());
        application.createNewEvent(EventConstants.REMOVE_ITEM_FROM_CART, LocalDateTime.now());
        application.createNewEvent(EventConstants.PAYMENT, LocalDateTime.now());

        application.createNewEvent(EventConstants.CHECKOUT, LocalDateTime.now().minusHours(1));

        final List<EventData> lastMinuteEvents = application.lastMinuteEvents();

        assertThat(lastMinuteEvents, hasSize(3));
    }

    @Test
    public void lastHourEvents() {
        application.createNewEvent(EventConstants.ADD_ITEM_TO_CART, LocalDateTime.now().minusMinutes(59));
        application.createNewEvent(EventConstants.ADD_ITEM_TO_CART, LocalDateTime.now());

        application.createNewEvent(EventConstants.PAYMENT, LocalDateTime.now().minusHours(2));

        final List<EventData> lastHourEvents = application.lastHourEvents();

        assertThat(lastHourEvents, hasSize(2));
    }

    @Test
    public void lastDayEvents() {
        application.createNewEvent(EventConstants.ADD_ITEM_TO_CART, LocalDateTime.now().minusHours(23));
        application.createNewEvent(EventConstants.ADD_ITEM_TO_CART, LocalDateTime.now());

        application.createNewEvent(EventConstants.PAYMENT, LocalDateTime.now().minusHours(25));

        final List<EventData> lastDayEvents = application.lastDayEvents();

        assertThat(lastDayEvents, hasSize(2));
    }

    @Test
    public void putOneThousandEventsAndCheckSizeInMultithreading() throws InterruptedException {
        final int exceptedTasks = 1000;
        final CountDownLatch count = new CountDownLatch(exceptedTasks);

        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService service = Executors.newFixedThreadPool(processors * 2);

        IntStream.range(0, exceptedTasks)
                .forEach(c -> service.submit(getTaskWithNowTime(count)));
        service.shutdown();

        count.await();

        final List<EventData> lastMinuteEvents = application.lastMinuteEvents();

        assertThat(lastMinuteEvents, hasSize(exceptedTasks));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putIncorrectEventName() {
        application.createNewEvent("incorrectEventName", LocalDateTime.now());
    }

    private Runnable getTaskWithNowTime(CountDownLatch count) {
        return () -> {
            application.createNewEvent(EventConstants.PAYMENT, LocalDateTime.now());
            count.countDown();
        };
    }

}