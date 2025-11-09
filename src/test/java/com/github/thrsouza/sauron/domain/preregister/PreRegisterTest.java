package com.github.thrsouza.sauron.domain.preregister;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PreRegister Domain Entity Tests")
class PreRegisterTest {

    @Nested
    @DisplayName("Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create PreRegister with valid data and PENDING status")
        void shouldCreatePreRegisterWithValidData() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";

            // When
            PreRegister preRegister = PreRegister.create(document, name, email);

            // Then
            assertNotNull(preRegister);
            assertNotNull(preRegister.id());
            assertEquals(document, preRegister.document());
            assertEquals(name, preRegister.name());
            assertEquals(email, preRegister.email());
            assertEquals(PreRegisterStatus.PENDING, preRegister.status());
            assertNotNull(preRegister.createdAt());
            assertNotNull(preRegister.updatedAt());
            assertEquals(preRegister.createdAt(), preRegister.updatedAt());
        }
    }

    @Nested
    @DisplayName("Approval Tests")
    class ApprovalTests {

        @Test
        @DisplayName("Should approve pending PreRegister and update timestamp")
        void shouldApprovePendingPreRegister() throws InterruptedException {
            // Given
            PreRegister preRegister = PreRegister.create("12345678900", "John Doe", "john.doe@example.com");
            Instant createdAt = preRegister.createdAt();
            
            // Small delay to ensure updatedAt is different
            Thread.sleep(1);

            // When
            preRegister.approve();

            // Then
            assertEquals(PreRegisterStatus.APPROVED, preRegister.status());
            assertTrue(preRegister.updatedAt().isAfter(createdAt));
        }

        @Test
        @DisplayName("Should throw exception when approving non-pending PreRegister")
        void shouldThrowExceptionWhenApprovingNonPending() {
            // Already approved
            PreRegister approvedPreRegister = PreRegister.create("12345678900", "John Doe", "john.doe@example.com");
            approvedPreRegister.approve();
            
            IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> approvedPreRegister.approve()
            );
            assertEquals("Pre-register status is not pending", exception1.getMessage());

            // Already rejected
            PreRegister rejectedPreRegister = PreRegister.create("98765432100", "Jane Doe", "jane@example.com");
            rejectedPreRegister.reject();
            
            IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> rejectedPreRegister.approve()
            );
            assertEquals("Pre-register status is not pending", exception2.getMessage());
        }
    }

    @Nested
    @DisplayName("Rejection Tests")
    class RejectionTests {

        @Test
        @DisplayName("Should reject pending PreRegister and update timestamp")
        void shouldRejectPendingPreRegister() throws InterruptedException {
            // Given
            PreRegister preRegister = PreRegister.create("12345678900", "John Doe", "john.doe@example.com");
            Instant createdAt = preRegister.createdAt();
            
            // Small delay to ensure updatedAt is different
            Thread.sleep(1);

            // When
            preRegister.reject();

            // Then
            assertEquals(PreRegisterStatus.REJECTED, preRegister.status());
            assertTrue(preRegister.updatedAt().isAfter(createdAt));
        }

        @Test
        @DisplayName("Should throw exception when rejecting non-pending PreRegister")
        void shouldThrowExceptionWhenRejectingNonPending() {
            // Already rejected
            PreRegister rejectedPreRegister = PreRegister.create("12345678900", "John Doe", "john.doe@example.com");
            rejectedPreRegister.reject();
            
            IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> rejectedPreRegister.reject()
            );
            assertEquals("Pre-register status is not pending", exception1.getMessage());

            // Already approved
            PreRegister approvedPreRegister = PreRegister.create("98765432100", "Jane Doe", "jane@example.com");
            approvedPreRegister.approve();
            
            IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> approvedPreRegister.reject()
            );
            assertEquals("Pre-register status is not pending", exception2.getMessage());
        }
    }

    @Nested
    @DisplayName("Snapshot Tests")
    class SnapshotTests {

        @Test
        @DisplayName("Should restore PreRegister from snapshot")
        void shouldRestorePreRegisterFromSnapshot() {
            // Given
            UUID id = UUID.randomUUID();
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            PreRegisterStatus status = PreRegisterStatus.APPROVED;
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            PreRegister.Snapshot snapshot = new PreRegister.Snapshot(
                id, document, name, email, status, createdAt, updatedAt
            );

            // When
            PreRegister preRegister = PreRegister.fromSnapshot(snapshot);

            // Then
            assertNotNull(preRegister);
            assertEquals(id, preRegister.id());
            assertEquals(document, preRegister.document());
            assertEquals(name, preRegister.name());
            assertEquals(email, preRegister.email());
            assertEquals(status, preRegister.status());
            assertEquals(createdAt, preRegister.createdAt());
            assertEquals(updatedAt, preRegister.updatedAt());
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should prevent any state transitions from APPROVED")
        void shouldPreventTransitionsFromApproved() {
            // Given
            PreRegister preRegister = PreRegister.create("12345678900", "John Doe", "john.doe@example.com");
            preRegister.approve();

            // Then
            assertThrows(IllegalArgumentException.class, () -> preRegister.approve());
            assertThrows(IllegalArgumentException.class, () -> preRegister.reject());
        }

        @Test
        @DisplayName("Should prevent any state transitions from REJECTED")
        void shouldPreventTransitionsFromRejected() {
            // Given
            PreRegister preRegister = PreRegister.create("12345678900", "John Doe", "john.doe@example.com");
            preRegister.reject();

            // Then
            assertThrows(IllegalArgumentException.class, () -> preRegister.approve());
            assertThrows(IllegalArgumentException.class, () -> preRegister.reject());
        }
    }
}
