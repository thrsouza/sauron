package com.github.thrsouza.sauron.application.usecases.preregister;

public record CreatePreRegisterUseCaseInput(
    String document, 
    String name, 
    String email) {}
