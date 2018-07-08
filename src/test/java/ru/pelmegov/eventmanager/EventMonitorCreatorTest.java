package ru.pelmegov.eventmanager;

import org.junit.Test;
import ru.pelmegov.eventmanager.core.StandardEventMonitor;
import ru.pelmegov.eventmanager.constants.EventConstants;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class EventMonitorCreatorTest {

    @Test
    public void createMonitorCreatorWithDefaultEventParameters() {
        final List<String> eventConstants =
                StandardEventMonitor.createStandardEventMonitor().getEventArguments().getEventConstants();

        assertThat(eventConstants, hasSize(4));

        assertThat(eventConstants, hasItem(EventConstants.ADD_ITEM_TO_CART));
        assertThat(eventConstants, hasItem(EventConstants.REMOVE_ITEM_FROM_CART));
        assertThat(eventConstants, hasItem(EventConstants.CHECKOUT));
        assertThat(eventConstants, hasItem(EventConstants.PAYMENT));
    }

}