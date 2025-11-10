package com.github.thrsouza.sauron.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.infrastructure.web.dto.RegisterCustomerRequest;
import com.github.thrsouza.sauron.infrastructure.web.dto.RegisterCustomerResponse;
import com.github.thrsouza.sauron.infrastructure.web.mapper.CustomerWebMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {
    
    private final CreateCustomerUseCase createCustomerUseCase;
    private final CustomerWebMapper mapper;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase, CustomerWebMapper mapper) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.mapper = mapper;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<RegisterCustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        CreateCustomerUseCase.Output output = createCustomerUseCase.handle(mapper.toUseCaseInput(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toWebResponse(output));
    }
}
