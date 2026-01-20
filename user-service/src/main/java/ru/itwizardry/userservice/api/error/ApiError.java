package ru.itwizardry.userservice.api.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "ApiError", description = "Standard API error response")
public record ApiError(

        @Schema(
                type = "string",
                format = "date-time",
                description = "Timestamp in ISO-8601",
                example = "2026-01-19T16:14:53.635Z"
        )
        OffsetDateTime timestamp,

        @Schema(
                description = "HTTP status code",
                example = "404",
                minimum = "400",
                maximum = "599"
        )
        int status,

        @Schema(
                description = "Short error title",
                example = "Duplicate Email"
        )
        String error,

        @Schema(
                description = "Human readable error message",
                example = "User with email already exists: mikhail@test.com"
        )
        String message,

        @Schema(
                description = "Stable machine-readable error code",
                example = "EMAIL_ALREADY_EXISTS"
        )
        String code,

        @Schema(
                description = "Request path",
                example = "/api/users"
        )
        String path
) {
}
