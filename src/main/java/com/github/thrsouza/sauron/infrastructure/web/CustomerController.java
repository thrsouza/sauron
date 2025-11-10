package com.github.thrsouza.sauron.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {
    private final CreateCustomerUseCase createCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @Transactional
    @PostMapping(value = "/register")
    public ResponseEntity<CreateCustomerUseCase.Output> create(@RequestBody CreateCustomerUseCase.Input input) {
        CreateCustomerUseCase.Output output = createCustomerUseCase.handle(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
}
