package com.github.thrsouza.sauron.application.customer;

import java.util.Optional;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEventPublisher;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;

public class EvaluateCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final DomainEventPublisher domainEventPublisher;

    public EvaluateCustomerUseCase(CustomerRepository customerRepository, DomainEventPublisher domainEventPublisher) {
        this.customerRepository = customerRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    public void handle(EvaluateCustomerUseCase.Input input) {
        Optional<Customer> existingCustomer = customerRepository.findById(input.customerId());

        if (existingCustomer.isPresent()) {
            var customer = existingCustomer.get();

            // Fake approval or rejection with delay
            if (Math.random() > 0.5) {
                customer.approve();
            } else {
                customer.reject();
            }

            customerRepository.save(customer);
            domainEventPublisher.publishAll(customer.pullDomainEvents());
               
        }
    }

    public record Input(UUID customerId) {}
}
