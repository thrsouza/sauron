package com.github.thrsouza.sauron.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CreatePreRegisterService {

    public Output execute(Input input) {
        return new Output(UUID.randomUUID());
    }

    public record Input(String document, String name, String email) { }

    public record Output(UUID id) { }
}
