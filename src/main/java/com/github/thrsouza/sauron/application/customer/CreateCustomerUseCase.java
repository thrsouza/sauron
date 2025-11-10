package com.github.thrsouza.sauron.application.customer;

import java.util.Optional;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEventPublisher;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;

public class CreateCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CreateCustomerUseCase(CustomerRepository customerRepository, DomainEventPublisher domainEventPublisher) {
        this.customerRepository = customerRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    public CreateCustomerUseCase.Output handle(CreateCustomerUseCase.Input input) {
        Optional<Customer> existingCustomer = customerRepository.findByDocument(input.document());

        if (existingCustomer.isPresent()) {
            return new Output(existingCustomer.get().id());
        }

        Customer customer = Customer.create(
            input.document(),
            input.name(),
            input.email());

        customerRepository.save(customer);
        domainEventPublisher.publishAll(customer.pullDomainEvents());

        return new Output(customer.id());
    }

    public record Input(String document, String name, String email) {}

    public record Output(UUID id) {}
}
