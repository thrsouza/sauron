package com.github.thrsouza.sauron.infrastructure.messaging;

import java.util.Objects;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.domain.customer.events.CustomerApproved;
import com.github.thrsouza.sauron.domain.customer.events.CustomerCreated;
import com.github.thrsouza.sauron.domain.customer.events.CustomerRejected;

import tools.jackson.databind.ObjectMapper;

@Component
public class Consumer {
    private final ObjectMapper objectMapper;

    public Consumer(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @EventListener
    public void consume(CustomerCreated event) {
        System.out.println("Customer created: " + objectMapper.writeValueAsString(event));
    }

    @EventListener
    public void consume(CustomerApproved event) {
        System.out.println("Customer approved: " + objectMapper.writeValueAsString(event));
    }

    @EventListener
    public void consume(CustomerRejected event) {
        System.out.println("Customer rejected: " + objectMapper.writeValueAsString(event));
    }
}
