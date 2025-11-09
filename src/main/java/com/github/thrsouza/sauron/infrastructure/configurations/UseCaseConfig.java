package com.github.thrsouza.sauron.infrastructure.configurations;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.thrsouza.sauron.application.repositories.PreRegisterRepository;
import com.github.thrsouza.sauron.application.usecases.preregister.CreatePreRegisterUseCase;

@Configuration
public class UseCaseConfig {

    private final PreRegisterRepository preRegisterRepository;

    public UseCaseConfig(PreRegisterRepository preRegisterRepository) {
        this.preRegisterRepository = Objects.requireNonNull(preRegisterRepository);
    }

    @Bean
    public CreatePreRegisterUseCase createPreRegisterUseCase() {
        return new CreatePreRegisterUseCase(preRegisterRepository);
    }
    
}
