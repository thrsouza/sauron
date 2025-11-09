package com.github.thrsouza.sauron.domain.preregister;

import java.time.Instant;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.DomainEntity;

public class PreRegister implements DomainEntity {

    private final UUID id;
    private final String document;
    private final String name;
    private final String email;
    private PreRegisterStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private PreRegister(UUID id, String document, String name, String email, PreRegisterStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PreRegister create(String document, String name, String email) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        
        return new PreRegister(id, document, name, email, PreRegisterStatus.PENDING, now, now);
    }

    public void approve() {
        if (this.status != PreRegisterStatus.PENDING) {
            throw new IllegalArgumentException("Pre-register status is not pending");
        }

        if (this.status == PreRegisterStatus.APPROVED) {
            throw new IllegalArgumentException("Pre-register is already approved");
        }

        this.status = PreRegisterStatus.APPROVED;
        this.updatedAt = Instant.now();
    }

    public void reject() {
        if (this.status != PreRegisterStatus.PENDING) {
            throw new IllegalArgumentException("Pre-register status is not pending");
        }

        if (this.status == PreRegisterStatus.REJECTED) {
            throw new IllegalArgumentException("Pre-register is already rejected");
        }

        this.status = PreRegisterStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public UUID id() {
        return id;
    }

    public String document() {
        return document;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public PreRegisterStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public record Snapshot(
        UUID id, 
        String document, 
        String name, 
        String email, 
        PreRegisterStatus status, 
        Instant createdAt, 
        Instant updatedAt) {}

    public static PreRegister fromSnapshot(Snapshot snapshot) {
        return new PreRegister(
            snapshot.id(),
            snapshot.document(),
            snapshot.name(),
            snapshot.email(),
            snapshot.status(),
            snapshot.createdAt(),
            snapshot.updatedAt());
    }
}
