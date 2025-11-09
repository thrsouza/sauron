package com.github.thrsouza.sauron.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PreRegister {
    @Id
    private UUID id;

    @Column(nullable = false, length = 32, unique = true, updatable = false)
    private String document;

    @Column(nullable = false, length = 128, updatable = false)
    private String name;

    @Column(nullable = false, length = 128, updatable = false)
    private String email;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public static PreRegister create(String document, String name, String email) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        PreRegister preRegister = new PreRegister();
        preRegister.setId(id);
        preRegister.setDocument(document);
        preRegister.setName(name);
        preRegister.setEmail(email);
        preRegister.setCreatedAt(now);
        preRegister.setUpdatedAt(now);

        return preRegister;
    }
}
