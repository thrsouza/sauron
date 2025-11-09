package com.github.thrsouza.sauron.domain;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();
    String eventType();
    Instant eventOccurredAt();
}
