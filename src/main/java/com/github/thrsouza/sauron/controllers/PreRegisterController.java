package com.github.thrsouza.sauron.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.thrsouza.sauron.services.CreatePreRegisterService;

@RestController
public class PreRegisterController {

    private final CreatePreRegisterService createPreRegisterService;

    public PreRegisterController(CreatePreRegisterService createPreRegisterService) {
        this.createPreRegisterService = createPreRegisterService;
    }

    @PostMapping("/api/pre-register")
    public ResponseEntity<CreatePreRegisterService.Output> create(@RequestBody CreatePreRegisterService.Input input) {
        CreatePreRegisterService.Output output = createPreRegisterService.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
    
}
