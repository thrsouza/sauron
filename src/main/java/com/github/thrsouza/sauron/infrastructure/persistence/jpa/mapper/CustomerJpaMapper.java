package com.github.thrsouza.sauron.infrastructure.persistence.jpa.mapper;

import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.infrastructure.persistence.jpa.entity.CustomerJpaEntity;

import org.springframework.stereotype.Component;

@Component
public class CustomerJpaMapper {
    
    public CustomerJpaEntity toEntity(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.id());
        entity.setDocument(customer.document());
        entity.setName(customer.name());
        entity.setEmail(customer.email());
        entity.setStatus(customer.status());
        entity.setCreatedAt(customer.createdAt());
        entity.setUpdatedAt(customer.updatedAt());
        return entity;
    }

    public Customer toDomain(CustomerJpaEntity entity) {
        Customer.Snapshot snapshot = new Customer.Snapshot(
            entity.getId(),
            entity.getDocument(),
            entity.getName(),
            entity.getEmail(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
        return Customer.fromSnapshot(snapshot);
    }
}
