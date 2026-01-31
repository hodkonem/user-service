# user-service

**user-service** — backend-сервис управления пользователями, реализующий REST CRUD API с поддержкой **HATEOAS** и публикацией доменных событий в Kafka для асинхронной интеграции с другими сервисами (например, `notification-service`).

Проект реализован в production-стиле с валидацией, единым форматом ошибок, Swagger-документацией и event-driven интеграцией.

---

## 🚀 Features

- REST CRUD API для пользователей (DTO-based)
- HATEOAS (hypermedia links в ответах)
- Валидация входных данных (`@Valid`)
- Единый формат ошибок API
- Публикация Kafka-событий:
  - `CREATED`
  - `DELETED`
- PostgreSQL + Liquibase
- Swagger / OpenAPI (Springdoc)
- Интеграционные и контроллерные тесты

---

## 🧩 Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web / Validation / Data JPA / HATEOAS
- Apache Kafka
- PostgreSQL
- Liquibase
- MapStruct
- JUnit 5 / MockMvc
- Docker (local dev)
- Gradle

---

## ⚙️ Configuration (Spring Cloud Config)

`user-service` uses **Spring Cloud Config** to load configuration from a centralized configuration repository.

### Default configuration source
- **Config Server:** `http://localhost:8888`
- **Config label (branch):** `develop`

The service expects configuration files to be stored in a separate **config-repo**, organized by application name and profile.

### Overriding configuration source

You can override the config server or label via environment variables:

```bash
CONFIG_SERVER_URL=http://localhost:8888
SPRING_CLOUD_CONFIG_LABEL=main
````

Example local run with overridden label:

```bash
SPRING_CLOUD_CONFIG_LABEL=main ./gradlew bootRun
```

### Profiles

* `default` – production-like configuration loaded from Config Server
* `test` – used for unit and controller tests
* `kafka-it` – used for Kafka integration tests (Testcontainers)

---

## 📦 API Overview (HATEOAS)

### Create user

```http
POST /api/users
```

**Request**

```json
{
  "name": "Mikhail",
  "email": "mikhail@test.com",
  "age": 30
}
```

**Response — 201 Created**

```json
{
  "id": 1,
  "name": "Mikhail",
  "email": "mikhail@test.com",
  "age": 30,
  "createdAt": "2026-01-20T13:31:06",
  "_links": {
    "self": { "href": "http://localhost:8080/api/users/1" },
    "users": { "href": "http://localhost:8080/api/users" },
    "update": { "href": "http://localhost:8080/api/users/1" },
    "delete": { "href": "http://localhost:8080/api/users/1" }
  }
}
```

---

### Get user by id

```http
GET /api/users/{id}
```

---

## ❌ Error Handling

```json
{
  "timestamp": "2026-01-20T13:50:50+03:00",
  "status": 400,
  "error": "Validation Error",
  "message": "email: Email must be valid",
  "code": "VALIDATION_ERROR",
  "path": "/api/users"
}
```

---

## 📣 Kafka Integration

* **Topic:** `user.notifications`
* **Producer:** `user-service`
* **Consumer:** `notification-service`

---

## 🛠 Local Development

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 👤 Author

Mikhail Latypov
GitHub: [https://github.com/hodkonem](https://github.com/hodkonem)
