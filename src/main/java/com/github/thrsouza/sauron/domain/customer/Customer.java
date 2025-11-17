package com.github.thrsouza.sauron.domain.customer;

import java.time.Instant;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEntity;
import com.github.thrsouza.sauron.domain.customer.events.CustomerApproved;
import com.github.thrsouza.sauron.domain.customer.events.CustomerCreated;
import com.github.thrsouza.sauron.domain.customer.events.CustomerRejected;

public class Customer extends DomainEntity {
    
    private final UUID id;
    private final String document;
    private final String name;
    private final String email;
    private CustomerStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Customer(UUID id, String document, String name, String email, CustomerStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Customer create(String document, String name, String email) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        
        Customer customer = new Customer(id, document, name, email, CustomerStatus.PENDING, now, now);
        customer.recordDomainEvent(new CustomerCreated(customer.id()));

        return customer;
    }

    public void evaluate(int score) {
        if (score > 700) {
            approve();
        } else {
            reject();
        }
    }

    private void approve() {
        if (this.status != CustomerStatus.PENDING) {
            throw new IllegalArgumentException("Customer status is not pending");
        }

        if (this.status == CustomerStatus.APPROVED) {
            throw new IllegalArgumentException("Customer is already approved");
        }

        this.status = CustomerStatus.APPROVED;
        this.updatedAt = Instant.now();

        this.recordDomainEvent(new CustomerApproved(this.id()));
    }

    private void reject() {
        if (this.status != CustomerStatus.PENDING) {
            throw new IllegalArgumentException("Customer status is not pending");
        }

        if (this.status == CustomerStatus.REJECTED) {
            throw new IllegalArgumentException("Customer is already rejected");
        }

        this.status = CustomerStatus.REJECTED;
        this.updatedAt = Instant.now();

        this.recordDomainEvent(new CustomerRejected(this.id()));
    }

    public UUID id() {
        return id;
    }

    public String document() {
        return document;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public CustomerStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public record Snapshot(
        UUID id, 
        String document, 
        String name, 
        String email, 
        CustomerStatus status, 
        Instant createdAt, 
        Instant updatedAt) {}

    public static Customer fromSnapshot(Snapshot snapshot) {
        return new Customer(
            snapshot.id(),
            snapshot.document(),
            snapshot.name(),
            snapshot.email(),
            snapshot.status(),
            snapshot.createdAt(),
            snapshot.updatedAt());
    }
}
