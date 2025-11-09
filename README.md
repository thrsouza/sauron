Sauron
======

Sauron is a Spring Boot sample service that captures customer pre-registrations and keeps a single, authoritative view of who has asked to join. Much like the Eye of Sauron watches every corner of Middle-earth, this service keeps an unblinking eye on incoming leads so none slip through unnoticed.

![Sauron](./assets/sauron.webp "Sauron")

Why “Sauron”?
-------------

The story behind the name is a playful nod to Tolkien’s Eye of Sauron. In the books, the Eye is relentless, always searching for any sign of disturbance. In this project, the eye focuses on prospects: every pre-registration is spotted instantly, deduplicated, and tracked from the moment it appears. The service acts as the watchtower for new customers, making sure the business always knows who is waiting at the gates.

Features
--------

- Idempotent pre-registration flow powered by `CreatePreRegisterUseCase`, which reuses the same identifier when the same document is submitted again.
- Ports and Adapters architecture layering:
  - Domain models live in `src/main/java/com/github/thrsouza/sauron/domain`.
  - Application use cases and interfaces are in `application`.
  - Infrastructure adapters (REST controller, JPA repository, configuration) are in `infrastructure`.
- Persistence handled by Spring Data JPA on top of an in-memory H2 database (ideal for demos and tests).
- Lightweight REST API that can be extended with approval/rejection flows as the product grows.

Tech Stack
----------

- Java 25
- Spring Boot 4.0.0-SNAPSHOT (Web MVC + Data JPA)
- H2 in-memory database
- Lombok for boilerplate reduction
- Maven wrapper for reproducible builds

Getting Started
---------------

### Prerequisites

- Java Development Kit (JDK) 25
- Maven 3.9+ (optional if you rely on the bundled `mvnw` wrapper)

### Running the application

```bash
./mvnw spring-boot:run
```

By default the service starts on `http://localhost:8080` and uses an in-memory H2 database defined in `src/main/resources/application.properties`. Schema changes are managed automatically (`spring.jpa.hibernate.ddl-auto=update`), making local development effortless.

### Executing tests

```bash
./mvnw test
```

REST API
--------

`POST /api/pre-register`

- Purpose: Create (or reuse) a customer pre-registration record.
- Request body:

```json
{
  "document": "00000000000",
  "name": "Frodo Baggins",
  "email": "frodo@shire.me"
}
```

- Successful response (`201 Created`):

```json
{
  "id": "0f8fad5b-d9cb-469f-a165-70867728950e"
}
```

If a pre-register already exists for the given `document`, the service returns the existing identifier. This makes the endpoint safe to call multiple times without creating duplicates.

Project Layout
--------------

```text
src/main/java/com/github/thrsouza/sauron/
├── SauronApplication.java               # Spring Boot entry point
├── application/                         # Use cases and repository interfaces
├── domain/                              # Domain entities and value objects
└── infrastructure/                      # REST controllers, persistence adapters
```

Next Steps
----------

- Introduce endpoints to approve or reject pending pre-registrations.
- Add persistence migrations (e.g., Flyway) when moving beyond the in-memory database.
- Integrate observability (logging/metrics) so the Eye can report what it sees.

License
-------

This project is distributed under the [MIT License](LICENSE), granting broad permission to use, modify, and distribute the code with proper attribution.
