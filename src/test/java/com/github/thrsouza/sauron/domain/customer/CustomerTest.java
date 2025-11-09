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
        @DisplayName("Should approve pending Customer and update timestamp")
        void shouldApprovePendingCustomer() throws InterruptedException {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            Instant createdAt = customer.createdAt();
            
            // Small delay to ensure updatedAt is different
            Thread.sleep(1);

            // When
            customer.approve();

            // Then
            assertEquals(CustomerStatus.APPROVED, customer.status());
            assertTrue(customer.updatedAt().isAfter(createdAt));
        }

        @Test
        @DisplayName("Should throw exception when approving non-pending Customer")
        void shouldThrowExceptionWhenApprovingNonPending() {
            // Already approved
            Customer approvedCustomer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            approvedCustomer.approve();
            
            IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> approvedCustomer.approve()
            );
            assertEquals("Customer status is not pending", exception1.getMessage());

            // Already rejected
            Customer rejectedCustomer = Customer.create("98765432100", "Jane Doe", "jane@example.com");
            rejectedCustomer.reject();
            
            IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> rejectedCustomer.approve()
            );
            assertEquals("Customer status is not pending", exception2.getMessage());
        }
    }

    @Nested
    @DisplayName("Rejection Tests")
    class RejectionTests {

        @Test
        @DisplayName("Should reject pending Customer and update timestamp")
        void shouldRejectPendingCustomer() throws InterruptedException {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            Instant createdAt = customer.createdAt();
            
            // Small delay to ensure updatedAt is different
            Thread.sleep(1);

            // When
            customer.reject();

            // Then
            assertEquals(CustomerStatus.REJECTED, customer.status());
            assertTrue(customer.updatedAt().isAfter(createdAt));
        }

        @Test
        @DisplayName("Should throw exception when rejecting non-pending Customer")
        void shouldThrowExceptionWhenRejectingNonPending() {
            // Already rejected
            Customer rejectedCustomer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            rejectedCustomer.reject();
            
            IllegalArgumentException exception1 = assertThrows(
                IllegalArgumentException.class,
                () -> rejectedCustomer.reject()
            );
            assertEquals("Customer status is not pending", exception1.getMessage());

            // Already approved
            Customer approvedCustomer = Customer.create("98765432100", "Jane Doe", "jane@example.com");
            approvedCustomer.approve();
            
            IllegalArgumentException exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> approvedCustomer.reject()
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
            customer.approve();

            // Then
            assertThrows(IllegalArgumentException.class, () -> customer.approve());
            assertThrows(IllegalArgumentException.class, () -> customer.reject());
        }

        @Test
        @DisplayName("Should prevent any state transitions from REJECTED")
        void shouldPreventTransitionsFromRejected() {
            // Given
            Customer customer = Customer.create("12345678900", "John Doe", "john.doe@example.com");
            customer.reject();

            // Then
            assertThrows(IllegalArgumentException.class, () -> customer.approve());
            assertThrows(IllegalArgumentException.class, () -> customer.reject());
        }
    }
}
