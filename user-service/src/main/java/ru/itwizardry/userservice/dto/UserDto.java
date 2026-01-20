package ru.itwizardry.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "UserDto", description = "User representation")
public record UserDto(

        @Schema(description = "User id", example = "1")
        Long id,

        @Schema(description = "User name", example = "Ivan Petrov")
        String name,

        @Schema(description = "User email", example = "ivan.petrov@example.com")
        String email,

        @Schema(description = "User age", example = "25", minimum = "1", maximum = "150")
        int age,

        @Schema(description = "Creation timestamp", example = "2026-01-19T15:48:43.944")
        LocalDateTime createdAt
) {}
