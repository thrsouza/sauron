package com.github.thrsouza.sauron.domain.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Domain Entity Tests")
class CustomerTest {
    
    @Nested
    @DisplayName("Creation Tests")
    class CreationTests {

        @Test
        @DisplayName("Should create Customer with valid data and PENDING status")
        void shouldCreateCustomerWithValidData() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";

            // When
            Customer customer = Customer.create(document, name, email);

            // Then
            assertNotNull(customer);
            assertNotNull(customer.id());
            assertEquals(document, customer.document());
            assertEquals(name, customer.name());
            assertEquals(email, customer.email());
            assertEquals(CustomerStatus.PENDING, customer.status());
            assertNotNull(customer.createdAt());
            assertNotNull(customer.updatedAt());
            assertEquals(customer.createdAt(), customer.updatedAt());
        }
    }

    @Nested
    @DisplayName("Approval Tests")
    class ApprovalTests {

        @Test
        @DisplayName("Should approve pending Customer with high score and update timestamp")
        void shouldApprovePendingCustomerWithHighScore() throws InterruptedException {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            Instant createdAt = customer.createdAt();
            int highScore = 800; // Score > 700 should approve
            
            // Small delay to ensure updatedAt is different
            Thread.sleep(1);

            // When
            customer.evaluate(highScore);

            // Then
            assertEquals(CustomerStatus.APPROVED, customer.status());
            assertTrue(customer.updatedAt().isAfter(createdAt));
        }

        @Test
        @DisplayName("Should throw exception when evaluating non-pending Customer for approval")
        void shouldThrowExceptionWhenEvaluatingNonPendingForApproval() {
            // Already approved
            Customer approvedCustomer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            approvedCustomer.evaluate(800);
            
            IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> approvedCustomer.evaluate(800)
            );
            assertEquals("Customer is already approved", exception1.getMessage());

            // Already rejected
            Customer rejectedCustomer = Customer.create("98765432100", "Jane Doe", "jane@example.com");
            rejectedCustomer.evaluate(600);
            
            IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> rejectedCustomer.evaluate(800)
            );
            assertEquals("Customer status is not pending", exception2.getMessage());
        }
    }

    @Nested
    @DisplayName("Rejection Tests")
    class RejectionTests {

        @Test
        @DisplayName("Should reject pending Customer with low score and update timestamp")
        void shouldRejectPendingCustomerWithLowScore() throws InterruptedException {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            Instant createdAt = customer.createdAt();
            int lowScore = 600; // Score <= 700 should reject
            
            // Small delay to ensure updatedAt is different
            Thread.sleep(1);

            // When
            customer.evaluate(lowScore);

            // Then
            assertEquals(CustomerStatus.REJECTED, customer.status());
            assertTrue(customer.updatedAt().isAfter(createdAt));
        }

        @Test
        @DisplayName("Should throw exception when evaluating non-pending Customer for rejection")
        void shouldThrowExceptionWhenEvaluatingNonPendingForRejection() {
            // Already rejected
            Customer rejectedCustomer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            rejectedCustomer.evaluate(600);
            
            IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> rejectedCustomer.evaluate(600)
            );
            assertEquals("Customer is already rejected", exception1.getMessage());

            // Already approved
            Customer approvedCustomer = Customer.create("98765432100", "Jane Doe", "jane@example.com");
            approvedCustomer.evaluate(800);
            
            IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> approvedCustomer.evaluate(600)
            );
            assertEquals("Customer status is not pending", exception2.getMessage());
        }
    }

    @Nested
    @DisplayName("Snapshot Tests")
    class SnapshotTests {

        @Test
        @DisplayName("Should restore Customer from snapshot")
        void shouldRestoreCustomerFromSnapshot() {
            // Given
            UUID id = UUID.randomUUID();
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CustomerStatus status = CustomerStatus.APPROVED;
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            Customer.Snapshot snapshot = new Customer.Snapshot(
                id, document, name, email, status, createdAt, updatedAt
            );

            // When
            Customer customer = Customer.fromSnapshot(snapshot);

            // Then
            assertNotNull(customer);
            assertEquals(id, customer.id());
            assertEquals(document, customer.document());
            assertEquals(name, customer.name());
            assertEquals(email, customer.email());
            assertEquals(status, customer.status());
            assertEquals(createdAt, customer.createdAt());
            assertEquals(updatedAt, customer.updatedAt());
        }
    }

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Should prevent any state transitions from APPROVED")
        void shouldPreventTransitionsFromApproved() {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            customer.evaluate(800); // Approve with high score

            // Then - Should not allow re-evaluation with any score
            assertThrows(IllegalArgumentException.class, () -> customer.evaluate(800));
            assertThrows(IllegalArgumentException.class, () -> customer.evaluate(600));
        }

        @Test
        @DisplayName("Should prevent any state transitions from REJECTED")
        void shouldPreventTransitionsFromRejected() {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            customer.evaluate(600); // Reject with low score

            // Then - Should not allow re-evaluation with any score
            assertThrows(IllegalArgumentException.class, () -> customer.evaluate(800));
            assertThrows(IllegalArgumentException.class, () -> customer.evaluate(600));
        }
    }
}
