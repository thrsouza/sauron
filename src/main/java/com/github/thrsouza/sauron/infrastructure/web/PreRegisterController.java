package com.github.thrsouza.sauron.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.thrsouza.sauron.application.usecases.preregister.CreatePreRegisterUseCase;
import com.github.thrsouza.sauron.application.usecases.preregister.CreatePreRegisterUseCaseInput;
import com.github.thrsouza.sauron.application.usecases.preregister.CreatePreRegisterUseCaseOutput;

@RestController
@RequestMapping(value = "/api/customer/register")
public class PreRegisterController {

    private final CreatePreRegisterUseCase createPreRegisterUseCase;

    public PreRegisterController(CreatePreRegisterUseCase createPreRegisterUseCase) {
        this.createPreRegisterUseCase = createPreRegisterUseCase;
    }

    @PostMapping
    public ResponseEntity<CreatePreRegisterUseCaseOutput> create(@RequestBody CreatePreRegisterUseCaseInput input) {
        CreatePreRegisterUseCaseOutput output = createPreRegisterUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
    
}
