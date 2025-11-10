package com.github.thrsouza.sauron.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    protected void recordDomainEvent(DomainEvent domainEvent) {
        this.domainEvents.add(domainEvent);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> copyOfDomainEvents = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return copyOfDomainEvents;
    }
}
