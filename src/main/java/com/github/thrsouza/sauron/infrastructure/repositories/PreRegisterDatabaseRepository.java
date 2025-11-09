package com.github.thrsouza.sauron.infrastructure.repositories;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.thrsouza.sauron.application.repositories.PreRegisterRepository;
import com.github.thrsouza.sauron.domain.preregister.PreRegister;
import com.github.thrsouza.sauron.infrastructure.persistence.PreRegisterEntity;
import com.github.thrsouza.sauron.infrastructure.persistence.PreRegisterJpaRepository;

@Component
public class PreRegisterDatabaseRepository implements PreRegisterRepository {
    
    private final PreRegisterJpaRepository preRegisterJpaRepository;

    public PreRegisterDatabaseRepository(PreRegisterJpaRepository preRegisterJpaRepository) {
        this.preRegisterJpaRepository = preRegisterJpaRepository;
    }

    @Override
    public Optional<PreRegister> findByDocument(String document) {
        Optional<PreRegisterEntity> preRegisterEntity = preRegisterJpaRepository.findByDocument(document);

        if (preRegisterEntity.isPresent()) {
            return Optional.of(PreRegisterEntity.toDomain(preRegisterEntity.get()));
        }
        
        return Optional.empty();
    }

    @Override
    public void save(PreRegister preRegister) {
        PreRegisterEntity preRegisterEntity = PreRegisterEntity.of(preRegister);
        preRegisterJpaRepository.save(preRegisterEntity);
    }
}
