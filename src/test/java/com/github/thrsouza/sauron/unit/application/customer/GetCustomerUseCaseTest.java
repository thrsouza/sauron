package com.github.thrsouza.sauron.unit.application.customer;

import com.github.thrsouza.sauron.application.customer.GetCustomerUseCase;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCustomerUseCase Tests")
class GetCustomerUseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private GetCustomerUseCase getCustomerUseCase;

    @Nested
    @DisplayName("Handle - Customer Found Tests")
    class CustomerFoundTests {

        @Test
        @DisplayName("Should return customer output when customer exists with PENDING status")
        void shouldReturnCustomerOutputWhenCustomerExistsWithPendingStatus() {
            // Given
            UUID customerId = UUID.randomUUID();
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            Instant now = Instant.now();

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, document, name, email,
                CustomerStatus.PENDING, now, now
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            GetCustomerUseCase.Input input = new GetCustomerUseCase.Input(customerId);

            // When
            GetCustomerUseCase.Output output = getCustomerUseCase.handle(input);

            // Then
            assertNotNull(output);
            assertEquals(customerId, output.id());
            assertEquals(document, output.document());
            assertEquals(name, output.name());
            assertEquals(email, output.email());
            assertEquals(CustomerStatus.PENDING, output.status());
            assertEquals(now, output.createdAt());
            assertEquals(now, output.updatedAt());
            verify(customerRepository, times(1)).findById(customerId);
        }

        @Test
        @DisplayName("Should return customer output when customer exists with APPROVED status")
        void shouldReturnCustomerOutputWhenCustomerExistsWithApprovedStatus() {
            // Given
            UUID customerId = UUID.randomUUID();
            String document = "98765432100";
            String name = "Jane Smith";
            String email = "jane.smith@example.com";
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, document, name, email,
                CustomerStatus.APPROVED, createdAt, updatedAt
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            GetCustomerUseCase.Input input = new GetCustomerUseCase.Input(customerId);

            // When
            GetCustomerUseCase.Output output = getCustomerUseCase.handle(input);

            // Then
            assertNotNull(output);
            assertEquals(customerId, output.id());
            assertEquals(document, output.document());
            assertEquals(name, output.name());
            assertEquals(email, output.email());
            assertEquals(CustomerStatus.APPROVED, output.status());
            assertEquals(createdAt, output.createdAt());
            assertEquals(updatedAt, output.updatedAt());
            verify(customerRepository, times(1)).findById(customerId);
        }

        @Test
        @DisplayName("Should return customer output when customer exists with REJECTED status")
        void shouldReturnCustomerOutputWhenCustomerExistsWithRejectedStatus() {
            // Given
            UUID customerId = UUID.randomUUID();
            String document = "11122233344";
            String name = "Bob Wilson";
            String email = "bob.wilson@example.com";
            Instant createdAt = Instant.now().minusSeconds(7200);
            Instant updatedAt = Instant.now();

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, document, name, email,
                CustomerStatus.REJECTED, createdAt, updatedAt
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            GetCustomerUseCase.Input input = new GetCustomerUseCase.Input(customerId);

            // When
            GetCustomerUseCase.Output output = getCustomerUseCase.handle(input);

            // Then
            assertNotNull(output);
            assertEquals(customerId, output.id());
            assertEquals(CustomerStatus.REJECTED, output.status());
            verify(customerRepository, times(1)).findById(customerId);
        }

        @Test
        @DisplayName("Should preserve timestamp differences between creation and update")
        void shouldPreserveTimestampDifferences() {
            // Given
            UUID customerId = UUID.randomUUID();
            Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");
            Instant updatedAt = Instant.parse("2025-01-01T12:30:00Z");

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, "12345678900", "Test User", "test@example.com",
                CustomerStatus.APPROVED, createdAt, updatedAt
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            GetCustomerUseCase.Input input = new GetCustomerUseCase.Input(customerId);

            // When
            GetCustomerUseCase.Output output = getCustomerUseCase.handle(input);

            // Then
            assertNotNull(output);
            assertEquals(createdAt, output.createdAt());
            assertEquals(updatedAt, output.updatedAt());
            assertNotEquals(output.createdAt(), output.updatedAt());
        }
    }

    @Nested
    @DisplayName("Handle - Customer Not Found Tests")
    class CustomerNotFoundTests {

        @Test
        @DisplayName("Should return null when customer does not exist")
        void shouldReturnNullWhenCustomerDoesNotExist() {
            // Given
            UUID customerId = UUID.randomUUID();
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            GetCustomerUseCase.Input input = new GetCustomerUseCase.Input(customerId);

            // When
            GetCustomerUseCase.Output output = getCustomerUseCase.handle(input);

            // Then
            assertNull(output);
            verify(customerRepository, times(1)).findById(customerId);
        }

        @Test
        @DisplayName("Should return null for non-existent random UUID")
        void shouldReturnNullForNonExistentRandomUUID() {
            // Given
            UUID randomId = UUID.randomUUID();
            when(customerRepository.findById(randomId)).thenReturn(Optional.empty());

            GetCustomerUseCase.Input input = new GetCustomerUseCase.Input(randomId);

            // When
            GetCustomerUseCase.Output output = getCustomerUseCase.handle(input);

            // Then
            assertNull(output);
        }
    }
}
