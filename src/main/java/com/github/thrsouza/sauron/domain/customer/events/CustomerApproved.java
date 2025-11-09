package com.github.thrsouza.sauron.domain.customer.events;

import java.time.Instant;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEvent;

public record CustomerApproved(
    UUID customerId,
    UUID eventId,
    Instant eventOccurredAt) implements DomainEvent {

    public CustomerApproved(UUID customerId) {
        this(customerId, UUID.randomUUID(), Instant.now());
    }

    @Override
    public String eventType() {
        return "SAURON.CUSTOMER_APPROVED";
    }
    
}
