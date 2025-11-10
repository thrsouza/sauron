package com.github.thrsouza.sauron.infrastructure.messaging.kafka.adapter;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.domain.DomainEvent;
import com.github.thrsouza.sauron.domain.DomainEventPublisher;

@Component
public class DomainEventPublisherAdapter implements DomainEventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisherAdapter.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DomainEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishAll(Collection<DomainEvent> events) {
        events.forEach(this::publish);
    }

    private void publish(DomainEvent event) {
        String topic = event.eventType();
        
        kafkaTemplate.send(topic, event)
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    handlePublishFailure(event, topic, exception);
                } else {
                    handlePublishSuccess(event, topic, result);
                }
            });
    }

    private void handlePublishSuccess(DomainEvent event, String topic, SendResult<String, Object> result) {
        log.info("📤 Published event to Kafka - Topic: {}, Event: {}, Partition: {}, Offset: {}",
            topic,
            event.getClass().getSimpleName(),
            result.getRecordMetadata().partition(),
            result.getRecordMetadata().offset()
        );
    }

    private void handlePublishFailure(DomainEvent event, String topic, Throwable exception) {
        log.error("❌ Failed to publish event to Kafka - Topic: {}, Event: {}, Error: {}",
            topic,
            event.getClass().getSimpleName(),
            exception.getMessage(),
            exception
        );
    }
}
