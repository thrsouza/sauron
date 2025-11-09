package com.github.thrsouza.sauron.application.customer;

public record CreateCustomerUseCaseInput(
    String document, 
    String name, 
    String email) {}
