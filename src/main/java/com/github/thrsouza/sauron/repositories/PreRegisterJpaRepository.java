package com.github.thrsouza.sauron.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.thrsouza.sauron.models.PreRegister;

@Repository
public interface PreRegisterJpaRepository extends JpaRepository<PreRegister, UUID> {
    
    Optional<PreRegister> findByDocument(String document);

}
