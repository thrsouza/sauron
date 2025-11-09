package com.github.thrsouza.sauron.infrastructure.repositories;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.infrastructure.persistence.CustomerEntity;
import com.github.thrsouza.sauron.infrastructure.persistence.CustomerJpaRepository;

@Component
public class CustomerSpringDataRepository implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;

    public CustomerSpringDataRepository(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        Optional<CustomerEntity> customerEntity = customerJpaRepository.findByDocument(document);

        if (customerEntity.isPresent()) {
            return Optional.of(CustomerEntity.toDomain(customerEntity.get()));
        }
        
        return Optional.empty();
    }

    @Override
    public void save(Customer customer) {
        CustomerEntity customerEntity = CustomerEntity.of(customer);
        customerJpaRepository.save(customerEntity);
    }
}
