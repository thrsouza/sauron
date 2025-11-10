package com.github.thrsouza.sauron.application.customer;

import com.github.thrsouza.sauron.application.DomainEventBus;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCustomerUseCase Tests")
class CreateCustomerUseCaseTest {
    @Mock
    private DomainEventBus domainEventBus;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreateCustomerUseCase createCustomerUseCase;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Nested
    @DisplayName("Execute - New Customer Tests")
    class ExecuteNewCustomerTests {

        @Test
        @DisplayName("Should create new Customer when document does not exist")
        void shouldCreateNewCustomerWhenDocumentDoesNotExist() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CreateCustomerUseCase.Input input = new CreateCustomerUseCase.Input(document, name, email);

            when(customerRepository.findByDocument(document)).thenReturn(Optional.empty());

            // When
            CreateCustomerUseCase.Output output = createCustomerUseCase.handle(input);

            // Then
            assertNotNull(output);
            assertNotNull(output.id());
            verify(customerRepository, times(1)).findByDocument(document);
            verify(customerRepository, times(1)).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should save Customer with correct data and return UUID")
        void shouldSaveCustomerWithCorrectData() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CreateCustomerUseCase.Input input = new CreateCustomerUseCase.Input(document, name, email);

            when(customerRepository.findByDocument(document)).thenReturn(Optional.empty());

            // When
            CreateCustomerUseCase.Output output = createCustomerUseCase.handle(input);

            // Then
            verify(customerRepository).save(customerCaptor.capture());
            Customer savedCustomer = customerCaptor.getValue();

            assertNotNull(savedCustomer);
            assertNotNull(savedCustomer.id());
            assertEquals(document, savedCustomer.document());
            assertEquals(name, savedCustomer.name());
            assertEquals(email, savedCustomer.email());
            assertEquals(CustomerStatus.PENDING, savedCustomer.status());
            assertNotNull(savedCustomer.createdAt());
            assertNotNull(savedCustomer.updatedAt());
            assertEquals(savedCustomer.id(), output.id());
        }
    }

    @Nested
    @DisplayName("Execute - Existing Customer Tests")
    class ExecuteExistingCustomerTests {

        @Test
        @DisplayName("Should return existing Customer UUID without saving")
        void shouldReturnExistingCustomerUUIDWithoutSaving() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CreateCustomerUseCase.Input input = new CreateCustomerUseCase.Input(document, name, email);

            UUID existingId = UUID.randomUUID();
            Customer.Snapshot snapshot = new Customer.Snapshot(
                existingId, document, "Jane Doe", "jane@example.com",
                CustomerStatus.PENDING, Instant.now(), Instant.now()
            );
            Customer existing = Customer.fromSnapshot(snapshot);

            when(customerRepository.findByDocument(document)).thenReturn(Optional.of(existing));

            // When
            CreateCustomerUseCase.Output output = createCustomerUseCase.handle(input);

            // Then
            assertNotNull(output);
            assertEquals(existingId, output.id());
            verify(customerRepository, times(1)).findByDocument(document);
            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should return existing UUID regardless of status")
        void shouldReturnExistingUUIDRegardlessOfStatus() {
            // Given
            String document = "12345678900";
            CreateCustomerUseCase.Input input = new CreateCustomerUseCase.Input(
                document, "John Doe", "john@example.com"
            );

            UUID approvedId = UUID.randomUUID();
            Customer.Snapshot approvedSnapshot = new Customer.Snapshot(
                approvedId, document, "John Doe", "john@example.com",
                CustomerStatus.APPROVED, Instant.now(), Instant.now()
            );
            Customer approvedCustomer = Customer.fromSnapshot(approvedSnapshot);

            when(customerRepository.findByDocument(document)).thenReturn(Optional.of(approvedCustomer));

            // When
            CreateCustomerUseCase.Output output = createCustomerUseCase.handle(input);

            // Then
            assertEquals(approvedId, output.id());
            verify(customerRepository, never()).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle consecutive executions correctly")
        void shouldHandleConsecutiveExecutionsCorrectly() {
            // Given
            CreateCustomerUseCase.Input input1 = new CreateCustomerUseCase.Input(
                "11111111111", "User One", "user1@example.com"
            );
            CreateCustomerUseCase.Input input2 = new CreateCustomerUseCase.Input(
                "22222222222", "User Two", "user2@example.com"
            );

            when(customerRepository.findByDocument("11111111111")).thenReturn(Optional.empty());
            when(customerRepository.findByDocument("22222222222")).thenReturn(Optional.empty());

            // When
            CreateCustomerUseCase.Output output1 = createCustomerUseCase.handle(input1);
            CreateCustomerUseCase.Output output2 = createCustomerUseCase.handle(input2);

            // Then
            assertNotNull(output1.id());
            assertNotNull(output2.id());
            assertNotEquals(output1.id(), output2.id());
            verify(customerRepository, times(2)).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should return same UUID for duplicate document submissions")
        void shouldReturnSameUUIDForDuplicateDocument() {
            // Given
            String document = "12345678900";
            CreateCustomerUseCase.Input input = new CreateCustomerUseCase.Input(
                document, "John Doe", "john@example.com"
            );

            Customer existingCustomer = Customer.create(document, "John Doe", "john@example.com");
            when(customerRepository.findByDocument(document)).thenReturn(Optional.of(existingCustomer));

            // When
            CreateCustomerUseCase.Output output1 = createCustomerUseCase.handle(input);
            CreateCustomerUseCase.Output output2 = createCustomerUseCase.handle(input);

            // Then
            assertEquals(output1.id(), output2.id());
            verify(customerRepository, times(2)).findByDocument(document);
            verify(customerRepository, never()).save(any(Customer.class));
        }
    }
}
