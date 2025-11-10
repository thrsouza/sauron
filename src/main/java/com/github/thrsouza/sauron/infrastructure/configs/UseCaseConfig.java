package com.github.thrsouza.sauron.infrastructure.configs;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.thrsouza.sauron.application.DomainEventBus;
import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;

@Configuration
public class UseCaseConfig {
    private final DomainEventBus domainEventBus;
    private final CustomerRepository customerRepository;

    public UseCaseConfig(DomainEventBus domainEventBus, CustomerRepository customerRepository) {
        this.domainEventBus = Objects.requireNonNull(domainEventBus);
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new CreateCustomerUseCase(customerRepository, domainEventBus);
    }
}
