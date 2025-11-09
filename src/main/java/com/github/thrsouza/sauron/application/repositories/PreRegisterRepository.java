package com.github.thrsouza.sauron.application.repositories;

import java.util.Optional;

import com.github.thrsouza.sauron.domain.preregister.PreRegister;

public interface PreRegisterRepository {

    Optional<PreRegister> findByDocument(String document);
    
    void save(PreRegister preRegister);

}
