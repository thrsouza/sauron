package com.github.thrsouza.sauron.infrastructure.web.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterCustomerResponse(
    
    @JsonProperty("id")
    UUID customerId
) {}
