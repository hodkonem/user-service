package ru.itwizardry.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "UserCreateRequest", description = "Create user request payload")
public record UserCreateRequest(

        @Schema(description = "User name", example = "Ivan Petrov", maxLength = 100)
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Schema(description = "User email", example = "ivan.petrov@example.com", maxLength = 255)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @Schema(description = "User age", example = "25", minimum = "1", maximum = "150")
        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 150, message = "Age must be at most 150")
        int age
) {}
