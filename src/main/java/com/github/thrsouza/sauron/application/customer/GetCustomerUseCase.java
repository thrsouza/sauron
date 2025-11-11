package com.github.thrsouza.sauron.application.customer;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;

public class GetCustomerUseCase {
    
    private final CustomerRepository customerRepository;

    public GetCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public GetCustomerUseCase.Output handle(GetCustomerUseCase.Input input) {
        Optional<Customer> existingCustomer = customerRepository.findById(input.id());

        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();

            return new GetCustomerUseCase.Output(
                customer.id(),
                customer.document(),
                customer.name(),
                customer.email(),
                customer.status(),
                customer.createdAt(),
                customer.updatedAt());
        }

        return null;
    }

    public record Input(
        UUID id) {}

    public record Output(
        UUID id,
        String document,
        String name,
        String email,
        CustomerStatus status,
        Instant createdAt,
        Instant updatedAt) {}
}
