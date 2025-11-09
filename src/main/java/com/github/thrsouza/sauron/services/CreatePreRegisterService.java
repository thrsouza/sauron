package com.github.thrsouza.sauron.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.thrsouza.sauron.models.PreRegister;
import com.github.thrsouza.sauron.repositories.PreRegisterJpaRepository;

import jakarta.transaction.Transactional;

@Service
public class CreatePreRegisterService {

    private final PreRegisterJpaRepository preRegisterJpaRepository;

    public CreatePreRegisterService(PreRegisterJpaRepository preRegisterJpaRepository) {
        this.preRegisterJpaRepository = preRegisterJpaRepository;
    }

    @Transactional
    public Output execute(Input input) {

        Optional<PreRegister> existingPreRegister = preRegisterJpaRepository.findByDocument(input.document());

        if (existingPreRegister.isPresent()) {
            return new Output(existingPreRegister.get().getId());
        }

        PreRegister preRegister = PreRegister.create(
            input.document(), 
            input.name(), 
            input.email());

        preRegisterJpaRepository.save(preRegister);

        return new Output(preRegister.getId());
    }

    public record Input(String document, String name, String email) { }

    public record Output(UUID id) { }
}
