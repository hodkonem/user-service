package ru.itwizardry.userservice.api.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorDetail", description = "Additional error details (field-level or domain-specific)")
public record ApiErrorDetail(
        @Schema(description = "Field name (for validation errors)", example = "email")
        String field,

        @Schema(description = "Human-readable detail message", example = "Email must be valid")
        String message
) {}
