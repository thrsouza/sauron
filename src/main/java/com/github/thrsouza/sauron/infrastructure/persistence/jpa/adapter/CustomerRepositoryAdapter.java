package com.github.thrsouza.sauron.infrastructure.persistence.jpa.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.infrastructure.persistence.jpa.entity.CustomerJpaEntity;
import com.github.thrsouza.sauron.infrastructure.persistence.jpa.mapper.CustomerJpaMapper;
import com.github.thrsouza.sauron.infrastructure.persistence.jpa.repository.CustomerJpaRepository;

@Component
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerJpaMapper mapper;

    public CustomerRepositoryAdapter(CustomerJpaRepository customerJpaRepository, CustomerJpaMapper mapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        return customerJpaRepository.findByDocument(document)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public void save(Customer customer) {
        CustomerJpaEntity entity = mapper.toEntity(customer);
        customerJpaRepository.save(entity);
    }
}
