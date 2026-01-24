package ru.itwizardry.userservice.kafka.dto;

public record UserEventDto(
        UserOperation operation,
        String email
) {}
