package com.github.thrsouza.sauron.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Customer") 
@Table(name = "customers")
@Getter
@Setter
public class CustomerEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 32, unique = true, updatable = false)
    private String document;

    @Column(nullable = false, length = 128, updatable = false)
    private String name;

    @Column(nullable = false, length = 128, updatable = false)
    private String email;

    @Column(nullable = false)
    private CustomerStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

        public static CustomerEntity of(Customer customer) {
        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setId(customer.id());
        customerEntity.setDocument(customer.document());
        customerEntity.setName(customer.name());
        customerEntity.setEmail(customer.email());
        customerEntity.setStatus(customer.status());
        customerEntity.setCreatedAt(customer.createdAt());
        customerEntity.setUpdatedAt(customer.updatedAt());

        return customerEntity;
    }

    public static Customer toDomain(CustomerEntity customerEntity) {
        Customer.Snapshot snapshot = new Customer.Snapshot(
            customerEntity.getId(),
            customerEntity.getDocument(),
            customerEntity.getName(),
            customerEntity.getEmail(),
            customerEntity.getStatus(),
            customerEntity.getCreatedAt(),
            customerEntity.getUpdatedAt());

        return Customer.fromSnapshot(snapshot);
    }
}
