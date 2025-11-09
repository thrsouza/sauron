package com.github.thrsouza.sauron.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class PreRegister {
    @Id
    private final UUID id;

    @Column(nullable = false, length = 32, unique = true, updatable = false)
    private final String document;

    @Column(nullable = false, length = 128, updatable = false)
    private final String name;

    @Column(nullable = false, length = 128, unique = true, updatable = false)
    private final String email;

    @Column(nullable = false, updatable = false)
    private final Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private PreRegister(UUID id, String document, String name, String email, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PreRegister create(String document, String name, String email) {
        UUID id = UUID.randomUUID();

        Instant now = Instant.now();

        return new PreRegister(id, document, name, email, now, now);
    }
}
