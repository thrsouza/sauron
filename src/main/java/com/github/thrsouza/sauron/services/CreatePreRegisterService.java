package com.github.thrsouza.sauron.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.thrsouza.sauron.models.PreRegister;

@Service
public class CreatePreRegisterService {

    public Output execute(Input input) {
        PreRegister preRegister = PreRegister.create(
            input.document(), 
            input.name(), 
            input.email());
            
        return new Output(preRegister.id());
    }

    public record Input(String document, String name, String email) { }

    public record Output(UUID id) { }
}
