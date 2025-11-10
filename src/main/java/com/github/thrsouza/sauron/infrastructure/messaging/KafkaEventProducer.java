package com.github.thrsouza.sauron.infrastructure.messaging;

import java.util.Collection;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.application.DomainEventBus;
import com.github.thrsouza.sauron.domain.DomainEvent;

@Component
public class KafkaEventProducer implements DomainEventBus {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishAll(Collection<DomainEvent> events) {
        for (DomainEvent event : events) {
            kafkaTemplate.send(event.eventType(), event);
        }
    }
}
