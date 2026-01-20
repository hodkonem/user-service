# user-service

**user-service** — backend сервис управления пользователями, реализующий REST CRUD API и публикующий доменные события в Kafka для асинхронной интеграции с другими сервисами (например, `notification-service`).

Проект реализован в production-стиле с валидацией, единым форматом ошибок, Kafka-событиями и интеграционными тестами.

---

## 🚀 Features

- REST CRUD API для пользователей (DTO-based)
- Валидация входных данных (`@Valid`)
- Единый формат ошибок API
- Публикация Kafka-событий:
  - `CREATED`
  - `DELETED`
- Асинхронная интеграция с `notification-service`
- PostgreSQL + Liquibase
- Swagger / OpenAPI
- Интеграционные и контроллерные тесты

---

## 🧩 Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web / Validation / Data JPA
- Apache Kafka
- PostgreSQL
- Liquibase
- MapStruct
- JUnit 5 / MockMvc
- Docker (dev environment)
- Gradle

---

## 📦 API Overview

### Create user
```http
POST /api/users
````

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
  "createdAt": "2026-01-20T13:31:06"
}
```

---

### Get user by id

```http
GET /api/users/{id}
```

---

### Delete user

```http
DELETE /api/users/{id}
```

**Response — 204 No Content**

---

## ❌ Error Handling

Все ошибки возвращаются в **едином формате**:

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

### Supported error codes

| HTTP | Code                 | Description             |
| ---: | -------------------- | ----------------------- |
|  400 | VALIDATION_ERROR     | Invalid request payload |
|  404 | USER_NOT_FOUND       | User does not exist     |
|  409 | EMAIL_ALREADY_EXISTS | Duplicate email         |

---

## 📣 Kafka Integration

* **Topic:** `user.notifications`
* **Producer:** `user-service`
* **Consumer:** `notification-service`

### Event payload

```json
{
  "operation": "CREATED",
  "email": "mikhail@test.com"
}
```

Events are published **only after successful DB transaction**.

---

## 🧪 Tests

```bash
./gradlew clean test
```

* Controller tests (`@WebMvcTest`)
* Integration tests (`@SpringBootTest`)
* Kafka flow verified end-to-end with MailHog

**Status:** ✅ 100% passing (14 tests)

---

## 🛠 Local Development

### Required services

* PostgreSQL
* Kafka
* MailHog

### Local endpoints

| Service    | URL                                            |
| ---------- | ---------------------------------------------- |
| User API   | [http://localhost:8080](http://localhost:8080) |
| Kafka UI   | [http://localhost:8089](http://localhost:8089) |
| MailHog UI | [http://localhost:8025](http://localhost:8025) |

---

## 📚 API Documentation

Swagger UI available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔖 Release

Current stable version:

```
v1.0.0
```

---

## 🧠 Notes

* Service is designed for **microservice architecture**
* No direct coupling with notification logic
* Kafka used for async, event-driven communication
* Ready for further extension (UPDATE events, retries, idempotency)

---

## 👤 Author

Mikhail Latypov
GitHub: [https://github.com/hodkonem](https://github.com/hodkonem)

```

