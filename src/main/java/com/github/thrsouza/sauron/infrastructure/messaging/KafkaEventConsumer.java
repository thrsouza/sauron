package com.github.thrsouza.sauron.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.domain.customer.events.CustomerApproved;
import com.github.thrsouza.sauron.domain.customer.events.CustomerCreated;
import com.github.thrsouza.sauron.domain.customer.events.CustomerRejected;

@Component
public class KafkaEventConsumer {
    @KafkaListener(topics = "SAURON.CUSTOMER_CREATED", groupId = "${spring.kafka.consumer.group-id}")
    public void onCustomerCreated(@Payload CustomerCreated event) {
        System.out.printf("📥 Received CustomerCreated: customerId=%s%n",event.customerId());
    }

    @KafkaListener(topics = "SAURON.CUSTOMER_APPROVED", groupId = "${spring.kafka.consumer.group-id}")
    public void onCustomerApproved(@Payload CustomerApproved event) {
        System.out.printf("📥 Received CustomerApproved: customerId=%s%n",event.customerId());
    }

    @KafkaListener(topics = "SAURON.CUSTOMER_REJECTED", groupId = "${spring.kafka.consumer.group-id}")
    public void onCustomerRejected(@Payload CustomerRejected event) {
        System.out.printf("📥 Received CustomerRejected: customerId=%s%n",event.customerId());
    }
}
