package com.github.thrsouza.sauron.infrastructure.web.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;

public record GetCustomerResponse(
    @JsonAlias("id")
    UUID id,

    @JsonAlias("document")
    String document,

    @JsonAlias("name")
    String name,

    @JsonAlias("email")
    String email,

    @JsonAlias("status")
    CustomerStatus status,

    @JsonAlias("created_at")
    Instant createdAt,

    @JsonAlias("updated_at")
    Instant updatedAt
) {}
