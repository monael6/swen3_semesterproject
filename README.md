# Paperless DMS - SWEN3 Semester Project (Group D)

Document management system for archiving documents, with OCR, AI summaries, tagging and
full-text search (added sprint by sprint).

**Sprint 1:** project setup, REST API (use case *Document Upload*), data access layer
with JPA/Hibernate on PostgreSQL (repository pattern), unit tests with a mocked database,
`docker-compose.yml` for REST server + database, and the additional use case **Tags**
(extra entity `Tag`, many-to-many with `Document`).

## Tech stack

- Java 25 (JDK LTS), Spring Boot 3.5, Maven (wrapper included: `mvnw` / `mvnw.cmd`)
- Spring Data JPA / Hibernate, PostgreSQL 17
- springdoc-openapi (Swagger UI), JUnit 5, Mockito, H2 (tests only), JaCoCo
- Docker / Docker Compose

## Run

### Everything in Docker (REST server + PostgreSQL)

```bash
docker compose up --build
```

- REST API: http://localhost:8080/api/documents
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

### From VS Code / locally

1. Start only the database: `docker compose up -d paperless-db`
2. Run `PaperlessApplication` (Run/Debug in VS Code) or `./mvnw spring-boot:run`
   (Windows: `mvnw.cmd spring-boot:run`).

Database settings can be overridden with the environment variables `DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USER`, `DB_PASSWORD` (defaults: `localhost`, `5432`, `paperless`, `paperless`, `paperless`).

## Tests

```bash
./mvnw verify
```

Unit tests mock the repositories (Mockito) or use an in-memory H2 database, so no
PostgreSQL is needed. The coverage report is written to `target/site/jacoco/index.html`.

### Trying out the endpoints

- `http/paperless.http` - requests for the VS Code *REST Client* extension (or IntelliJ HTTP client)
- `http/test-api.sh` - curl smoke test of all endpoints: `./http/test-api.sh` (Git Bash on Windows)
- `http/HelloWorld.pdf` - sample document

## REST API

The API is specified in [`src/main/resources/static/openapi.yaml`](src/main/resources/static/openapi.yaml)
(OpenAPI 3.0) and shown in Swagger UI.

| Method | Path | Description |
|---|---|---|
| POST | `/api/documents` | Upload a PDF (multipart: `file`, optional `title`) |
| GET | `/api/documents?title=` | List documents, optional title filter |
| GET | `/api/documents/{id}` | Get a document |
| PUT | `/api/documents/{id}` | Update the title |
| DELETE | `/api/documents/{id}` | Delete a document |
| GET | `/api/tags` | List tags |
| POST | `/api/tags` | Create a tag |
| DELETE | `/api/tags/{tagId}` | Delete a tag |
| PUT | `/api/documents/{id}/tags/{tagId}` | Assign a tag to a document |
| DELETE | `/api/documents/{id}/tags/{tagId}` | Remove a tag from a document |

Errors are returned as RFC 7807 problem details (`400`, `404`, `409`, `413`, `415`).

## Project structure

```
src/main/java/at/technikum/swen3/paperless
├── controller   REST layer (DocumentController, TagController)
├── service      business layer (DocumentService, TagService)
├── repository   data access layer (Spring Data JPA repositories)
├── entity       JPA entities (DocumentEntity, TagEntity)
├── dto          data transfer objects used by the REST layer
├── mapper       entity <-> DTO mapping
└── exception    custom exceptions + global exception handler
```
