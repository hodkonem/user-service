package ru.itwizardry.userservice.api.docs;

public final class SwaggerExamples {
    private SwaggerExamples() {}

    public static final String ERROR_EMAIL_EXISTS = """
        {
          "timestamp": "2026-01-19T16:14:53.635Z",
          "status": 409,
          "error": "Duplicate Email",
          "message": "User with email already exists: mikhail@test.com",
          "code": "EMAIL_ALREADY_EXISTS",
          "path": "/api/users"
        }
        """;

    public static final String ERROR_USER_NOT_FOUND = """
        {
          "timestamp": "2026-01-19T16:16:07.473Z",
          "status": 404,
          "error": "User Not Found",
          "message": "User with id 1 not found",
          "code": "USER_NOT_FOUND",
          "path": "/api/users/1"
        }
        """;

    public static final String ERROR_VALIDATION = """
        {
          "timestamp": "2026-01-19T16:20:10.000Z",
          "status": 400,
          "error": "Validation Error",
          "message": "email: Email must be valid, age: must be less than or equal to 150",
          "code": "VALIDATION_ERROR",
          "path": "/api/users"
        }
        """;
}
