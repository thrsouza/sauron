package com.github.thrsouza.sauron.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thrsouza.sauron.application.customer.CreateCustomerUseCase;
import com.github.thrsouza.sauron.application.customer.GetCustomerUseCase;
import com.github.thrsouza.sauron.domain.customer.CustomerStatus;
import com.github.thrsouza.sauron.infrastructure.web.controller.CustomerController;
import com.github.thrsouza.sauron.infrastructure.web.dto.GetCustomerResponse;
import com.github.thrsouza.sauron.infrastructure.web.dto.RegisterCustomerRequest;
import com.github.thrsouza.sauron.infrastructure.web.exception.GlobalExceptionHandler;
import com.github.thrsouza.sauron.infrastructure.web.mapper.CustomerWebMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController Integration Tests")
class CustomerControllerIntegrationTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @MockitoBean
        private CreateCustomerUseCase createCustomerUseCase;

        @MockitoBean
        private GetCustomerUseCase getCustomerUseCase;

        @MockitoBean
        private CustomerWebMapper customerWebMapper;

        @BeforeEach
        void setUp() {
                CustomerController controller = new CustomerController(
                                createCustomerUseCase,
                                getCustomerUseCase,
                                customerWebMapper);

                GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

                // Configure validation
                LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
                validator.afterPropertiesSet();

                mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setControllerAdvice(exceptionHandler)
                                .setValidator(validator)
                                .build();
        }

        @Nested
        @DisplayName("GET /api/customers/{id} Tests")
        class GetCustomerTests {

                @Test
                @DisplayName("Should return 200 with customer data when customer exists")
                void shouldReturn200WithCustomerDataWhenCustomerExists() throws Exception {
                        // Given
                        UUID customerId = UUID.randomUUID();
                        String document = "12345678900";
                        String name = "John Doe";
                        String email = "john.doe@example.com";
                        CustomerStatus status = CustomerStatus.PENDING;
                        Instant createdAt = Instant.now();
                        Instant updatedAt = Instant.now();

                        GetCustomerUseCase.Output useCaseOutput = new GetCustomerUseCase.Output(
                                        customerId, document, name, email, status, createdAt, updatedAt);

                        GetCustomerResponse expectedResponse = new GetCustomerResponse(
                                        customerId, document, name, email, status, createdAt, updatedAt);

                        when(getCustomerUseCase.handle(any(GetCustomerUseCase.Input.class)))
                                        .thenReturn(useCaseOutput);
                        when(customerWebMapper.toWebOutput(useCaseOutput))
                                        .thenReturn(expectedResponse);

                        // When & Then
                        mockMvc.perform(get("/api/customers/{id}", customerId))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(customerId.toString()))
                                        .andExpect(jsonPath("$.document").value(document))
                                        .andExpect(jsonPath("$.name").value(name))
                                        .andExpect(jsonPath("$.email").value(email))
                                        .andExpect(jsonPath("$.status").value(status.name()))
                                        .andExpect(jsonPath("$.createdAt").exists())
                                        .andExpect(jsonPath("$.updatedAt").exists());
                }

                @Test
                @DisplayName("Should return 404 when customer does not exist")
                void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
                        // Given
                        UUID customerId = UUID.randomUUID();

                        when(getCustomerUseCase.handle(any(GetCustomerUseCase.Input.class)))
                                        .thenReturn(null);

                        // When & Then
                        mockMvc.perform(get("/api/customers/{id}", customerId))
                                        .andExpect(status().isNotFound());
                }

                @Test
                @DisplayName("Should return customer with APPROVED status")
                void shouldReturnCustomerWithApprovedStatus() throws Exception {
                        // Given
                        UUID customerId = UUID.randomUUID();
                        GetCustomerUseCase.Output useCaseOutput = new GetCustomerUseCase.Output(
                                        customerId, "98765432100", "Jane Smith", "jane@example.com",
                                        CustomerStatus.APPROVED, Instant.now(), Instant.now());

                        GetCustomerResponse expectedResponse = new GetCustomerResponse(
                                        customerId, "98765432100", "Jane Smith", "jane@example.com",
                                        CustomerStatus.APPROVED, Instant.now(), Instant.now());

                        when(getCustomerUseCase.handle(any(GetCustomerUseCase.Input.class)))
                                        .thenReturn(useCaseOutput);
                        when(customerWebMapper.toWebOutput(useCaseOutput))
                                        .thenReturn(expectedResponse);

                        // When & Then
                        mockMvc.perform(get("/api/customers/{id}", customerId))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.status").value(CustomerStatus.APPROVED.name()));
                }
        }

        @Nested
        @DisplayName("POST /api/customers/register Tests")
        class RegisterCustomerTests {

                @Test
                @DisplayName("Should return 201 with Location header when customer is registered successfully")
                void shouldReturn201WithLocationHeaderWhenCustomerRegisteredSuccessfully() throws Exception {
                        // Given
                        UUID customerId = UUID.randomUUID();
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "11144477735", "John Doe", "john.doe@example.com");

                        CreateCustomerUseCase.Input useCaseInput = new CreateCustomerUseCase.Input(
                                        request.document(), request.name(), request.email());
                        CreateCustomerUseCase.Output useCaseOutput = new CreateCustomerUseCase.Output(customerId);

                        when(customerWebMapper.toUseCaseInput(request)).thenReturn(useCaseInput);
                        when(createCustomerUseCase.handle(useCaseInput)).thenReturn(useCaseOutput);

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isCreated())
                                        .andExpect(header().string("Location", "/api/customers/" + customerId));
                }

                @Test
                @DisplayName("Should return 400 when document is missing")
                void shouldReturn400WhenDocumentIsMissing() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        null, "John Doe", "john.doe@example.com");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.title").value("Validation failed"))
                                        .andExpect(jsonPath("$.message")
                                                        .value("One or more validation errors occurred"))
                                        .andExpect(jsonPath("$.errors.document").exists());
                }

                @Test
                @DisplayName("Should return 400 when name is missing")
                void shouldReturn400WhenNameIsMissing() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "12345678900", null, "john.doe@example.com");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.name").exists());
                }

                @Test
                @DisplayName("Should return 400 when email is missing")
                void shouldReturn400WhenEmailIsMissing() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "12345678900", "John Doe", null);

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.email").exists());
                }

                @Test
                @DisplayName("Should return 400 when document is blank")
                void shouldReturn400WhenDocumentIsBlank() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "", "John Doe", "john.doe@example.com");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.document").exists());
                }

                @Test
                @DisplayName("Should return 400 when document is invalid CPF format")
                void shouldReturn400WhenDocumentIsInvalidCPFFormat() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "123456789", "John Doe", "john.doe@example.com");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.document").exists());
                }

                @Test
                @DisplayName("Should return 400 when document has wrong size")
                void shouldReturn400WhenDocumentHasWrongSize() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "123456789000", "John Doe", "john.doe@example.com");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.document").exists());
                }

                @Test
                @DisplayName("Should return 400 when email is invalid format")
                void shouldReturn400WhenEmailIsInvalidFormat() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "12345678900", "John Doe", "invalid-email");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.email").exists());
                }

                @Test
                @DisplayName("Should return 400 when name exceeds max length")
                void shouldReturn400WhenNameExceedsMaxLength() throws Exception {
                        // Given
                        String longName = "a".repeat(129);
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "12345678900", longName, "john.doe@example.com");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.name").exists());
                }

                @Test
                @DisplayName("Should return 400 when email exceeds max length")
                void shouldReturn400WhenEmailExceedsMaxLength() throws Exception {
                        // Given
                        String longEmail = "a".repeat(120) + "@example.com";
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        "12345678900", "John Doe", longEmail);

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.email").exists());
                }

                @Test
                @DisplayName("Should return 400 when multiple validation errors occur")
                void shouldReturn400WhenMultipleValidationErrorsOccur() throws Exception {
                        // Given
                        RegisterCustomerRequest request = new RegisterCustomerRequest(
                                        null, null, "invalid-email");

                        // When & Then
                        mockMvc.perform(post("/api/customers/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.errors.document").exists())
                                        .andExpect(jsonPath("$.errors.name").exists())
                                        .andExpect(jsonPath("$.errors.email").exists());
                }
        }
}
