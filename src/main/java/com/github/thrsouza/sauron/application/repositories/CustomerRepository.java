package com.github.thrsouza.sauron.application.repositories;

import java.util.Optional;

import com.github.thrsouza.sauron.domain.customer.Customer;

public interface CustomerRepository {

    Optional<Customer> findByDocument(String document);
    
    void save(Customer customer);

}
