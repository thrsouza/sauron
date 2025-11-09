package com.github.thrsouza.sauron.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.github.thrsouza.sauron.domain.preregister.PreRegister;
import com.github.thrsouza.sauron.domain.preregister.PreRegisterStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "PreRegister") 
@Table(name = "pre_registers")
@Getter
@Setter
public class PreRegisterEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 32, unique = true, updatable = false)
    private String document;

    @Column(nullable = false, length = 128, updatable = false)
    private String name;

    @Column(nullable = false, length = 128, updatable = false)
    private String email;

    @Column(nullable = false)
    private PreRegisterStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public static PreRegisterEntity of(PreRegister preRegister) {
        PreRegisterEntity preRegisterEntity = new PreRegisterEntity();

        preRegisterEntity.setId(preRegister.id());
        preRegisterEntity.setDocument(preRegister.document());
        preRegisterEntity.setName(preRegister.name());
        preRegisterEntity.setEmail(preRegister.email());
        preRegisterEntity.setStatus(preRegister.status());
        preRegisterEntity.setCreatedAt(preRegister.createdAt());
        preRegisterEntity.setUpdatedAt(preRegister.updatedAt());

        return preRegisterEntity;
    }

    public static PreRegister toDomain(PreRegisterEntity preRegisterEntity) {
        PreRegister.Snapshot snapshot = new PreRegister.Snapshot(
            preRegisterEntity.getId(),
            preRegisterEntity.getDocument(),
            preRegisterEntity.getName(),
            preRegisterEntity.getEmail(),
            preRegisterEntity.getStatus(),
            preRegisterEntity.getCreatedAt(),
            preRegisterEntity.getUpdatedAt());

        return PreRegister.fromSnapshot(snapshot);
    }
}
