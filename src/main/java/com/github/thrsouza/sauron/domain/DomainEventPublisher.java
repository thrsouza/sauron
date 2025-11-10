package com.github.thrsouza.sauron.domain;

import java.util.Collection;

public interface DomainEventPublisher {
    void publishAll(Collection<DomainEvent> events);
}
