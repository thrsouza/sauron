package com.github.thrsouza.sauron.infrastructure.messaging.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.application.customer.EvaluateCustomerUseCase;
import com.github.thrsouza.sauron.domain.customer.events.CustomerApproved;
import com.github.thrsouza.sauron.domain.customer.events.CustomerCreated;
import com.github.thrsouza.sauron.domain.customer.events.CustomerRejected;

@Component
public class CustomerEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);
    
    private final EvaluateCustomerUseCase evaluateCustomerUseCase;

    public CustomerEventListener(EvaluateCustomerUseCase evaluateCustomerUseCase) {
        this.evaluateCustomerUseCase = evaluateCustomerUseCase;
    }

    @KafkaListener(
        topics = "sauron.customer-created",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCustomerCreated(
            @Payload CustomerCreated event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("📥 Received CustomerCreated event - CustomerId: {}, Topic: {}, Partition: {}, Offset: {}",
            event.customerId(), topic, partition, offset);
        
        try {
            evaluateCustomerUseCase.handle(new EvaluateCustomerUseCase.Input(event.customerId()));
            log.info("✅ Successfully processed CustomerCreated event - CustomerId: {}", event.customerId());
        } catch (Exception e) {
            log.error("❌ Error processing CustomerCreated event - CustomerId: {}, Error: {}",
                event.customerId(), e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
        topics = "sauron.customer-approved",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCustomerApproved(
            @Payload CustomerApproved event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("📥 Received CustomerApproved event - CustomerId: {}, Topic: {}, Partition: {}, Offset: {}",
            event.customerId(), topic, partition, offset);
    }

    @KafkaListener(
        topics = "sauron.customer-rejected",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCustomerRejected(
            @Payload CustomerRejected event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("📥 Received CustomerRejected event - CustomerId: {}, Topic: {}, Partition: {}, Offset: {}",
            event.customerId(), topic, partition, offset);
    }
}
