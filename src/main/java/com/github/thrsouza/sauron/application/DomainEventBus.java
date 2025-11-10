package com.github.thrsouza.sauron.application;

import java.util.Collection;

import com.github.thrsouza.sauron.domain.DomainEvent;

public interface DomainEventBus {
    void publishAll(Collection<DomainEvent> events);
}
