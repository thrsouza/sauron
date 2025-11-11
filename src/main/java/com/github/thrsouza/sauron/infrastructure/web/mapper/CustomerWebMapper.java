package com.github.thrsouza.sauron.infrastructure.web.mapper;

import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.infrastructure.web.dto.RegisterCustomerRequest;

@Component
public class CustomerWebMapper {

    public CreateCustomerUseCase.Input toUseCaseInput(RegisterCustomerRequest request) {
        return new CreateCustomerUseCase.Input(
            request.document(),
            request.name(),
            request.email()
        );
    }
}
