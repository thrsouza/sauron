package com.github.thrsouza.sauron.application.customer;

import java.util.Optional;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEventPublisher;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.domain.customer.CustomerScoreService;

public class EvaluateCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final CustomerScoreService customerScoreService;

    public EvaluateCustomerUseCase(CustomerRepository customerRepository, DomainEventPublisher domainEventPublisher, CustomerScoreService customerScoreService) {
        this.customerRepository = customerRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.customerScoreService = customerScoreService;
    }

    public void handle(EvaluateCustomerUseCase.Input input) {
        Optional<Customer> existingCustomer = customerRepository.findById(input.customerId());

        if (existingCustomer.isPresent()) {
            var customer = existingCustomer.get();

            int score = customerScoreService.getScoreByDocument(customer.document());
            customer.evaluate(score);

            customerRepository.save(customer);
            domainEventPublisher.publishAll(customer.pullDomainEvents());
        }
    }

    public record Input(UUID customerId) {}
}
