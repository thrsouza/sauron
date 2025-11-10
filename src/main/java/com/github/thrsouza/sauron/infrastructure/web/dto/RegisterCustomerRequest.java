package com.github.thrsouza.sauron.infrastructure.web.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
    
    @NotBlank(message = "Document is required")
    @Pattern(regexp = "\\d{11}", message = "Document must contain exactly 11 digits")
    @CPF(message = "Document must be a valid CPF")
    String document,
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 128, message = "Name must be between 3 and 128 characters")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 128, message = "Email must not exceed 128 characters")
    String email
) {}
