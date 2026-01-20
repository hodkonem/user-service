package ru.itwizardry.userservice.kafka.dto;

public record UserEventDto(
        String operation,
        String email
) {
}
