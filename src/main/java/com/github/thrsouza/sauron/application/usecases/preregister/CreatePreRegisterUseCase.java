package com.github.thrsouza.sauron.application.usecases.preregister;

import java.util.Optional;

import com.github.thrsouza.sauron.application.repositories.PreRegisterRepository;
import com.github.thrsouza.sauron.application.usecases.UseCase;
import com.github.thrsouza.sauron.domain.preregister.PreRegister;

public class CreatePreRegisterUseCase implements UseCase<CreatePreRegisterUseCaseInput, CreatePreRegisterUseCaseOutput> {

    private final PreRegisterRepository preRegisterRepository;

    public CreatePreRegisterUseCase(PreRegisterRepository preRegisterRepository) {
        this.preRegisterRepository = preRegisterRepository;
    }

    @Override
    public CreatePreRegisterUseCaseOutput execute(CreatePreRegisterUseCaseInput input) {
        Optional<PreRegister> existingPreRegister = preRegisterRepository.findByDocument(input.document());

        if (existingPreRegister.isPresent()) {
            return new CreatePreRegisterUseCaseOutput(existingPreRegister.get().id());
        }

        PreRegister preRegister = PreRegister.create(
            input.document(), 
            input.name(), 
            input.email());

        preRegisterRepository.save(preRegister);

        return new CreatePreRegisterUseCaseOutput(preRegister.id());
    }
}
