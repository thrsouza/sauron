package com.github.thrsouza.sauron.infrastructure.messaging;

import java.util.Collection;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.application.DomainEventBus;
import com.github.thrsouza.sauron.domain.DomainEvent;

@Component
public class Producer implements DomainEventBus {
    private final ApplicationEventPublisher eventPublisher;

    public Producer(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void publishAll(Collection<DomainEvent> events) {
        events.forEach(event -> eventPublisher.publishEvent(event));
        System.out.println("Published " + events.size() + " events");
    }
}
