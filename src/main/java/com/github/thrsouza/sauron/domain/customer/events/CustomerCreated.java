package com.github.thrsouza.sauron.domain.customer.events;

import java.time.Instant;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEvent;

public record CustomerCreated(
    UUID eventId,
    UUID customerId,
    Instant eventOccurredAt) implements DomainEvent {

    public CustomerCreated(UUID customerId) {
        this(UUID.randomUUID(), customerId, Instant.now());
    }

    @Override
    public String eventType() {
        return "sauron.customer-created";
    }
}
