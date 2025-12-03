Sauron
======

Sauron is a Spring Boot sample service that manages customer registrations and their approval workflow based on document (CPF) validation and credit score evaluation. Much like the Eye of Sauron watches every corner of Middle-earth, this service keeps an unblinking eye on customer registrations, ensuring each application is properly evaluated before approval or rejection.

![Sauron](./assets/sauron.png "Sauron")

Why "Sauron"?
-------------

The story behind the name is a playful nod to Tolkien's Eye of Sauron. In the books, the Eye is relentless, always searching for any sign of disturbance. In this project, the eye focuses on customer registrations: every submission is spotted instantly, deduplicated, and tracked from the moment it appears. The service acts as the watchtower for new customers, evaluating their documents and credit scores to determine who shall be approved or rejected.

Features
--------

- Idempotent customer registration flow powered by `CreateCustomerUseCase`, which reuses the same identifier when the same document is submitted again.
- Customer status management with three states: `PENDING` (awaiting evaluation), `APPROVED` (passed credit score evaluation), and `REJECTED` (failed evaluation).
- Event-driven architecture using Apache Kafka for asynchronous processing:
  - Domain events (`CustomerCreated`, `CustomerApproved`, `CustomerRejected`) published to Kafka topics.
  - Automatic credit score evaluation triggered by `CustomerCreated` events via `EvaluateCustomerUseCase`.
  - Decoupled event producers and consumers for scalable message processing.
- Credit score evaluation using external service integration:
  - `CustomerScoreService` domain interface defines the contract for credit score retrieval.
  - `CustomerScoreServiceHttpAdapter` simulates HTTP integration with external credit score provider.
  - Score-based approval logic: customers with score > 700 are approved, otherwise rejected.
- Hexagonal Architecture (Ports and Adapters) layering:
  - Domain models live in `src/main/java/com/github/thrsouza/sauron/domain`.
  - Application use cases and repository interfaces are in `application`.
  - Infrastructure adapters (REST controller, JPA repository, Kafka messaging, HTTP integration, configuration) are in `infrastructure`.
- Persistence handled by Spring Data JPA with PostgreSQL for production and H2 for tests.
- Database migrations managed by Flyway for version-controlled schema changes.
- Lightweight REST API with asynchronous event-driven credit score evaluation.
- Input validation using Jakarta Bean Validation with custom validators for Brazilian CPF documents.
- Global exception handling with structured error responses for validation failures.

Tech Stack
----------

- Java 25
- Spring Boot 4.0.0 (Web MVC + Data JPA + Kafka)
- Apache Kafka for event-driven messaging
- PostgreSQL for production database
- H2 in-memory database for tests
- Flyway for database migrations
- Jakarta Bean Validation with Hibernate Validator (including Brazilian CPF validation)
- SpringDoc OpenAPI with Swagger UI for interactive API documentation
- Jackson for JSON serialization
- Lombok for boilerplate reduction
- Maven wrapper for reproducible builds
- Docker Compose for local infrastructure setup (Kafka + PostgreSQL)

Getting Started
---------------

### Prerequisites

- Java Development Kit (JDK) 25
- Maven 3.9+ (optional if you rely on the bundled `mvnw` wrapper)
- Docker and Docker Compose (for running Kafka and PostgreSQL locally)

### Running the application

1. Start infrastructure services (Kafka and PostgreSQL) using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- Apache Kafka broker on `localhost:9097` with 3 partitions
- PostgreSQL database on `localhost:5432` with database `sauron` (user: `docker`, password: `docker`)

2. Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

By default the service starts on `http://localhost:8080` and uses PostgreSQL for persistence. Database schema is managed by Flyway migrations located in `src/main/resources/db/migration/`. The initial migration creates the `customers` table with proper constraints and indexes.

**Note:** For tests, the application uses H2 in-memory database configured via the `test` profile (`application-test.properties`), ensuring fast and isolated test execution.

### Stopping the infrastructure

```bash
docker-compose down
```

To also remove volumes (including database data):

```bash
docker-compose down -v
```

### Executing tests

```bash
./mvnw test
```

**Note:** If you encounter Mockito initialization errors with Java 25 on macOS (related to ByteBuddy agent attachment), this is a known compatibility issue. The domain tests (`CustomerTest`) and integration tests (`SauronApplicationTests`) should pass successfully. Use case tests may require Java 21 LTS or earlier for Mockito to work properly on macOS.

REST API
--------

### API Documentation

The service provides interactive API documentation powered by SpringDoc OpenAPI with Swagger UI. Once the application is running, you can access:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) - Modern, interactive API documentation interface
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) - Raw OpenAPI specification in JSON format

Swagger UI provides a user-friendly way to explore and test all available endpoints without needing external tools like Postman or curl.

### Event-Driven Flow

When a customer is registered:

1. The customer is created with `PENDING` status
2. A `CustomerCreated` event is published to the `sauron.customer-created` Kafka topic
3. The Kafka `Consumer` receives the event and triggers `EvaluateCustomerUseCase`
4. The use case retrieves the credit score from `CustomerScoreService` (simulated HTTP call with 3-second delay)
5. Customer is evaluated based on the score:
   - If score > 700: customer is approved and `CustomerApproved` event is published to `sauron.customer-approved`
   - If score ≤ 700: customer is rejected and `CustomerRejected` event is published to `sauron.customer-rejected`

This asynchronous architecture ensures the registration endpoint responds quickly while the evaluation happens in the background.

License
-------

This project is distributed under the [MIT License](LICENSE), granting broad permission to use, modify, and distribute the code with proper attribution.
