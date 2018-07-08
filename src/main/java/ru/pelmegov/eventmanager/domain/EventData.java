package ru.pelmegov.eventmanager.domain;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor
@ToString
public class EventData {
    String eventName;
    LocalDateTime eventTime;
}
