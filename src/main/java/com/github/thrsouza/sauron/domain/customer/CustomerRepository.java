package com.github.thrsouza.sauron.domain.customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Optional<Customer> findByDocument(String document);
    Optional<Customer> findById(UUID id);
    void save(Customer customer);
}
