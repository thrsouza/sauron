package com.github.thrsouza.sauron.infrastructure.web.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.application.customer.GetCustomerUseCase;
import com.github.thrsouza.sauron.infrastructure.web.dto.GetCustomerResponse;
import com.github.thrsouza.sauron.infrastructure.web.dto.RegisterCustomerRequest;
import com.github.thrsouza.sauron.infrastructure.web.mapper.CustomerWebMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {
    
    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final CustomerWebMapper mapper;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase, GetCustomerUseCase getCustomerUseCase, CustomerWebMapper mapper) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.mapper = mapper;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GetCustomerResponse> get(@PathVariable UUID id) {
        GetCustomerUseCase.Output output = getCustomerUseCase
            .handle(new GetCustomerUseCase.Input(id));

        if (output == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.toWebOutput(output));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterCustomerRequest request) {
        CreateCustomerUseCase.Output output = createCustomerUseCase
            .handle(mapper.toUseCaseInput(request));

        URI location = URI.create("/api/customers/" + output.id());
        
        return ResponseEntity.created(location).build();
    }
}
