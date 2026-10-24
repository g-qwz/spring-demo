# ToDo API

Simple REST API for managing ToDo entries, built with Spring Boot 4, Spring Data JPA and PostgreSQL.

> **Main focus of this project: hot reload.** The ToDo API is intentionally minimal — the goal is a containerized Spring Boot dev setup where code changes are picked up instantly, without manually rebuilding images or restarting containers.

## Hot reload

Hot reload is built from two pieces:

- **Docker Compose watch** (`develop.watch` in `compose.yaml`)
  - `./src` → synced to `/app/src` in the container, then the app restarts (`sync+restart`)
  - `build.gradle.kts` → image is rebuilt (`rebuild`), since dependencies changed
- **Spring Boot DevTools** — fast application restart and LiveReload server on port `35729` (exposed in `compose.yaml`)

Gradle caches live in the named `gradle` volume, so restarts and rebuilds don't re-download dependencies.

Start it with:

```bash
docker compose watch
```

Then edit any file under `src/` and watch the app restart in the logs.

## Tech stack

- Java 25
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, DevTools)
- PostgreSQL 17
- springdoc-openapi (Swagger UI)
- Lombok
- Gradle (Kotlin DSL)
- Docker Compose

## Getting started

### Prerequisites

- Docker with Docker Compose v2.22+ (for `watch`)
- JDK 25 (only for running outside Docker)

### Run with Docker (recommended)

```bash
docker compose up --build
```

For hot reload, use `docker compose watch` instead (see [Hot reload](#hot-reload)).

The app is available at http://localhost:8080 and PostgreSQL at `localhost:5432`.

### Run locally

Start only the database in Docker, then run the app with Gradle:

```bash
docker compose up -d db
./gradlew bootRun
```

The default datasource in `application.properties` points at `localhost:5432/app`.

### Configuration

Database settings can be overridden with environment variables (e.g. in a `.env` file next to `compose.yaml`):

| Variable            | Default    |
|---------------------|------------|
| `POSTGRES_DB`       | `app`      |
| `POSTGRES_USER`     | `postgres` |
| `POSTGRES_PASSWORD` | `postgres` |

## API

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI spec: http://localhost:8080/v3/api-docs

| Method   | Path             | Description          |
|----------|------------------|----------------------|
| `GET`    | `/api/todo`      | List all entries     |
| `GET`    | `/api/todo/{id}` | Get entry by id      |
| `POST`   | `/api/todo`      | Create entry         |
| `PUT`    | `/api/todo/{id}` | Update entry         |
| `DELETE` | `/api/todo/{id}` | Delete entry         |

Example request body:

```json
{
  "title": "Buy milk",
  "description": "2 liters, low fat",
  "done": false
}
```

`title` and `description` are required and must not be blank.

Example:

```bash
curl -X POST http://localhost:8080/api/todo \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"2 liters","done":false}'
```

## Tests

```bash
./gradlew test
```

## Project structure

```
src/main/java/de/test/spring/
├── Application.java       # Spring Boot entry point
├── OpenAPIConfig.java     # OpenAPI metadata
├── ToDoController.java    # REST endpoints
├── ToDoEntry.java         # JPA entity
└── ToDoRepo.java          # Spring Data repository
```
