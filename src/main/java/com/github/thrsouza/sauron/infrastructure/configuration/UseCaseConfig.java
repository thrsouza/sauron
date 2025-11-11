package com.github.thrsouza.sauron.infrastructure.configuration;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.application.customer.EvaluateCustomerUseCase;
import com.github.thrsouza.sauron.application.customer.GetCustomerUseCase;
import com.github.thrsouza.sauron.domain.DomainEventPublisher;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;

@Configuration
public class UseCaseConfig {
    
    private final DomainEventPublisher domainEventPublisher;
    private final CustomerRepository customerRepository;

    public UseCaseConfig(DomainEventPublisher domainEventPublisher, CustomerRepository customerRepository) {
        this.domainEventPublisher = Objects.requireNonNull(domainEventPublisher);
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new CreateCustomerUseCase(customerRepository, domainEventPublisher);
    }

    @Bean
    public EvaluateCustomerUseCase evaluateCustomerUseCase() {
        return new EvaluateCustomerUseCase(customerRepository, domainEventPublisher);
    }

    @Bean
    public GetCustomerUseCase getCustomerUseCase() {
        return new GetCustomerUseCase(customerRepository);
    }
}
