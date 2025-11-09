package com.github.thrsouza.sauron.application.usecases.customer;

public record CreateCustomerUseCaseInput(
    String document, 
    String name, 
    String email) {}
