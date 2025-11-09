package com.github.thrsouza.sauron.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCaseInput;
import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCaseOutput;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {
    private final CreateCustomerUseCase createCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<CreateCustomerUseCaseOutput> create(@RequestBody CreateCustomerUseCaseInput input) {
        CreateCustomerUseCaseOutput output = createCustomerUseCase.handle(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
    
}
