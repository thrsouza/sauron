package com.github.thrsouza.sauron.application.usecases.preregister;

import com.github.thrsouza.sauron.application.repositories.PreRegisterRepository;
import com.github.thrsouza.sauron.domain.preregister.PreRegister;
import com.github.thrsouza.sauron.domain.preregister.PreRegisterStatus;
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
@DisplayName("CreatePreRegisterUseCase Tests")
class CreatePreRegisterUseCaseTest {

    @Mock
    private PreRegisterRepository preRegisterRepository;

    @InjectMocks
    private CreatePreRegisterUseCase createPreRegisterUseCase;

    @Captor
    private ArgumentCaptor<PreRegister> preRegisterCaptor;

    @Nested
    @DisplayName("Execute - New PreRegister Tests")
    class ExecuteNewPreRegisterTests {

        @Test
        @DisplayName("Should create new PreRegister when document does not exist")
        void shouldCreateNewPreRegisterWhenDocumentDoesNotExist() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CreatePreRegisterUseCaseInput input = new CreatePreRegisterUseCaseInput(document, name, email);

            when(preRegisterRepository.findByDocument(document)).thenReturn(Optional.empty());

            // When
            CreatePreRegisterUseCaseOutput output = createPreRegisterUseCase.execute(input);

            // Then
            assertNotNull(output);
            assertNotNull(output.id());
            verify(preRegisterRepository, times(1)).findByDocument(document);
            verify(preRegisterRepository, times(1)).save(any(PreRegister.class));
        }

        @Test
        @DisplayName("Should save PreRegister with correct data and return UUID")
        void shouldSavePreRegisterWithCorrectData() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CreatePreRegisterUseCaseInput input = new CreatePreRegisterUseCaseInput(document, name, email);

            when(preRegisterRepository.findByDocument(document)).thenReturn(Optional.empty());

            // When
            CreatePreRegisterUseCaseOutput output = createPreRegisterUseCase.execute(input);

            // Then
            verify(preRegisterRepository).save(preRegisterCaptor.capture());
            PreRegister savedPreRegister = preRegisterCaptor.getValue();

            assertNotNull(savedPreRegister);
            assertNotNull(savedPreRegister.id());
            assertEquals(document, savedPreRegister.document());
            assertEquals(name, savedPreRegister.name());
            assertEquals(email, savedPreRegister.email());
            assertEquals(PreRegisterStatus.PENDING, savedPreRegister.status());
            assertNotNull(savedPreRegister.createdAt());
            assertNotNull(savedPreRegister.updatedAt());
            assertEquals(savedPreRegister.id(), output.id());
        }
    }

    @Nested
    @DisplayName("Execute - Existing PreRegister Tests")
    class ExecuteExistingPreRegisterTests {

        @Test
        @DisplayName("Should return existing PreRegister UUID without saving")
        void shouldReturnExistingPreRegisterUUIDWithoutSaving() {
            // Given
            String document = "12345678900";
            String name = "John Doe";
            String email = "john.doe@example.com";
            CreatePreRegisterUseCaseInput input = new CreatePreRegisterUseCaseInput(document, name, email);

            UUID existingId = UUID.randomUUID();
            PreRegister.Snapshot snapshot = new PreRegister.Snapshot(
                existingId, document, "Jane Doe", "jane@example.com",
                PreRegisterStatus.PENDING, Instant.now(), Instant.now()
            );
            PreRegister existing = PreRegister.fromSnapshot(snapshot);

            when(preRegisterRepository.findByDocument(document)).thenReturn(Optional.of(existing));

            // When
            CreatePreRegisterUseCaseOutput output = createPreRegisterUseCase.execute(input);

            // Then
            assertNotNull(output);
            assertEquals(existingId, output.id());
            verify(preRegisterRepository, times(1)).findByDocument(document);
            verify(preRegisterRepository, never()).save(any(PreRegister.class));
        }

        @Test
        @DisplayName("Should return existing UUID regardless of status")
        void shouldReturnExistingUUIDRegardlessOfStatus() {
            // Given
            String document = "12345678900";
            CreatePreRegisterUseCaseInput input = new CreatePreRegisterUseCaseInput(
                document, "John Doe", "john@example.com"
            );

            UUID approvedId = UUID.randomUUID();
            PreRegister.Snapshot approvedSnapshot = new PreRegister.Snapshot(
                approvedId, document, "John Doe", "john@example.com",
                PreRegisterStatus.APPROVED, Instant.now(), Instant.now()
            );
            PreRegister approvedPreRegister = PreRegister.fromSnapshot(approvedSnapshot);

            when(preRegisterRepository.findByDocument(document)).thenReturn(Optional.of(approvedPreRegister));

            // When
            CreatePreRegisterUseCaseOutput output = createPreRegisterUseCase.execute(input);

            // Then
            assertEquals(approvedId, output.id());
            verify(preRegisterRepository, never()).save(any(PreRegister.class));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle consecutive executions correctly")
        void shouldHandleConsecutiveExecutionsCorrectly() {
            // Given
            CreatePreRegisterUseCaseInput input1 = new CreatePreRegisterUseCaseInput(
                "11111111111", "User One", "user1@example.com"
            );
            CreatePreRegisterUseCaseInput input2 = new CreatePreRegisterUseCaseInput(
                "22222222222", "User Two", "user2@example.com"
            );

            when(preRegisterRepository.findByDocument("11111111111")).thenReturn(Optional.empty());
            when(preRegisterRepository.findByDocument("22222222222")).thenReturn(Optional.empty());

            // When
            CreatePreRegisterUseCaseOutput output1 = createPreRegisterUseCase.execute(input1);
            CreatePreRegisterUseCaseOutput output2 = createPreRegisterUseCase.execute(input2);

            // Then
            assertNotNull(output1.id());
            assertNotNull(output2.id());
            assertNotEquals(output1.id(), output2.id());
            verify(preRegisterRepository, times(2)).save(any(PreRegister.class));
        }

        @Test
        @DisplayName("Should return same UUID for duplicate document submissions")
        void shouldReturnSameUUIDForDuplicateDocument() {
            // Given
            String document = "12345678900";
            CreatePreRegisterUseCaseInput input = new CreatePreRegisterUseCaseInput(
                document, "John Doe", "john@example.com"
            );

            PreRegister existingPreRegister = PreRegister.create(document, "John Doe", "john@example.com");
            when(preRegisterRepository.findByDocument(document)).thenReturn(Optional.of(existingPreRegister));

            // When
            CreatePreRegisterUseCaseOutput output1 = createPreRegisterUseCase.execute(input);
            CreatePreRegisterUseCaseOutput output2 = createPreRegisterUseCase.execute(input);

            // Then
            assertEquals(output1.id(), output2.id());
            verify(preRegisterRepository, times(2)).findByDocument(document);
            verify(preRegisterRepository, never()).save(any(PreRegister.class));
        }
    }
}
