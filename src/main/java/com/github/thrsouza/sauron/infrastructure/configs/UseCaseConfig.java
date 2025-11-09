package com.github.thrsouza.sauron.infrastructure.configs;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;

@Configuration
public class UseCaseConfig {
    private final CustomerRepository customerRepository;

    public UseCaseConfig(CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new CreateCustomerUseCase(customerRepository);
    }
}
