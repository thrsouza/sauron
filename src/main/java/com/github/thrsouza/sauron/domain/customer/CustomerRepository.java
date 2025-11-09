package com.github.thrsouza.sauron.domain.customer;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findByDocument(String document);
    void save(Customer customer);
}
