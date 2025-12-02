package com.github.thrsouza.sauron.unit.application.customer;

import com.github.thrsouza.sauron.application.customer.EvaluateCustomerUseCase;
import com.github.thrsouza.sauron.domain.DomainEvent;
import com.github.thrsouza.sauron.domain.DomainEventPublisher;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;
import com.github.thrsouza.sauron.domain.customer.CustomerScoreService;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;
import com.github.thrsouza.sauron.domain.customer.events.CustomerApproved;
import com.github.thrsouza.sauron.domain.customer.events.CustomerRejected;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluateCustomerUseCase Tests")
class EvaluateCustomerUseCaseTest {

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerScoreService customerScoreService;

    @InjectMocks
    private EvaluateCustomerUseCase evaluateCustomerUseCase;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Captor
    private ArgumentCaptor<List<DomainEvent>> eventsCaptor;

    @Nested
    @DisplayName("Handle - Customer Evaluation Tests")
    class HandleCustomerEvaluationTests {

        @Test
        @DisplayName("Should evaluate and update customer when found")
        void shouldEvaluateAndUpdateCustomerWhenFound() {
            // Given
            UUID customerId = UUID.randomUUID();
            EvaluateCustomerUseCase.Input input = new EvaluateCustomerUseCase.Input(customerId);

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, "12345678900", "John Doe", "john@example.com",
                CustomerStatus.PENDING, Instant.now(), Instant.now()
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            // When
            evaluateCustomerUseCase.handle(input);

            // Then
            verify(customerRepository, times(1)).findById(customerId);
            verify(customerRepository, times(1)).save(any(Customer.class));
            verify(domainEventPublisher, times(1)).publishAll(any());
        }

        @Test
        @DisplayName("Should save customer with updated status after evaluation")
        void shouldSaveCustomerWithUpdatedStatus() {
            // Given
            UUID customerId = UUID.randomUUID();
            EvaluateCustomerUseCase.Input input = new EvaluateCustomerUseCase.Input(customerId);

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, "12345678900", "John Doe", "john@example.com",
                CustomerStatus.PENDING, Instant.now(), Instant.now()
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(customerScoreService.getScoreByDocument(customer.document())).thenReturn(800);
            
            // When
            evaluateCustomerUseCase.handle(input);

            // Then
            verify(customerRepository).save(customerCaptor.capture());
            Customer savedCustomer = customerCaptor.getValue();

            assertNotNull(savedCustomer);
            assertTrue(
                savedCustomer.status() == CustomerStatus.APPROVED || 
                savedCustomer.status() == CustomerStatus.REJECTED,
                "Customer status should be either APPROVED or REJECTED"
            );
        }

        @Test
        @DisplayName("Should publish domain events after evaluation")
        void shouldPublishDomainEventsAfterEvaluation() {
            // Given
            UUID customerId = UUID.randomUUID();
            EvaluateCustomerUseCase.Input input = new EvaluateCustomerUseCase.Input(customerId);

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, "12345678900", "John Doe", "john@example.com",
                CustomerStatus.PENDING, Instant.now(), Instant.now()
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            // When
            evaluateCustomerUseCase.handle(input);

            // Then
            verify(domainEventPublisher).publishAll(eventsCaptor.capture());
            List<DomainEvent> events = eventsCaptor.getValue();

            assertNotNull(events);
            assertFalse(events.isEmpty());
            
            DomainEvent event = events.get(0);
            assertTrue(
                event instanceof CustomerApproved || event instanceof CustomerRejected,
                "Event should be either CustomerApproved or CustomerRejected"
            );
        }

        @Test
        @DisplayName("Should not process when customer is not found")
        void shouldNotProcessWhenCustomerNotFound() {
            // Given
            UUID customerId = UUID.randomUUID();
            EvaluateCustomerUseCase.Input input = new EvaluateCustomerUseCase.Input(customerId);

            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            // When
            evaluateCustomerUseCase.handle(input);

            // Then
            verify(customerRepository, times(1)).findById(customerId);
            verify(customerRepository, never()).save(any(Customer.class));
            verify(domainEventPublisher, never()).publishAll(any());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle multiple evaluation requests independently")
        void shouldHandleMultipleEvaluationRequestsIndependently() {
            // Given
            UUID customerId1 = UUID.randomUUID();
            UUID customerId2 = UUID.randomUUID();
            
            EvaluateCustomerUseCase.Input input1 = new EvaluateCustomerUseCase.Input(customerId1);
            EvaluateCustomerUseCase.Input input2 = new EvaluateCustomerUseCase.Input(customerId2);

            Customer.Snapshot snapshot1 = new Customer.Snapshot(
                customerId1, "11111111111", "User One", "user1@example.com",
                CustomerStatus.PENDING, Instant.now(), Instant.now()
            );
            Customer.Snapshot snapshot2 = new Customer.Snapshot(
                customerId2, "22222222222", "User Two", "user2@example.com",
                CustomerStatus.PENDING, Instant.now(), Instant.now()
            );

            Customer customer1 = Customer.fromSnapshot(snapshot1);
            Customer customer2 = Customer.fromSnapshot(snapshot2);

            when(customerRepository.findById(customerId1)).thenReturn(Optional.of(customer1));
            when(customerRepository.findById(customerId2)).thenReturn(Optional.of(customer2));

            // When
            evaluateCustomerUseCase.handle(input1);
            evaluateCustomerUseCase.handle(input2);

            // Then
            verify(customerRepository, times(2)).findById(any(UUID.class));
            verify(customerRepository, times(2)).save(any(Customer.class));
            verify(domainEventPublisher, times(2)).publishAll(any());
        }

        @Test
        @DisplayName("Should not evaluate customer that is not in PENDING status")
        void shouldNotEvaluateNonPendingCustomer() {
            // Given
            UUID customerId = UUID.randomUUID();
            EvaluateCustomerUseCase.Input input = new EvaluateCustomerUseCase.Input(customerId);

            Customer.Snapshot snapshot = new Customer.Snapshot(
                customerId, "12345678900", "John Doe", "john@example.com",
                CustomerStatus.APPROVED, Instant.now(), Instant.now()
            );
            Customer customer = Customer.fromSnapshot(snapshot);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> {
                evaluateCustomerUseCase.handle(input);
            });
        }
    }
}
