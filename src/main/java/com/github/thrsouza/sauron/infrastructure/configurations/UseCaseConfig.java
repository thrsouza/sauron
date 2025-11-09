package com.github.thrsouza.sauron.infrastructure.configurations;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.thrsouza.sauron.application.repositories.CustomerRepository;
import com.github.thrsouza.sauron.application.usecases.customer.CreateCustomerUseCase;

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
