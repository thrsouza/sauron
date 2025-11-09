package com.github.thrsouza.sauron.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreRegisterJpaRepository extends JpaRepository<PreRegisterEntity, UUID> {
    
    Optional<PreRegisterEntity> findByDocument(String document);
    
}
