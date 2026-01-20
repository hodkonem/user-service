package ru.itwizardry.userservice.api.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itwizardry.userservice.exception.EmailAlreadyExistsException;
import ru.itwizardry.userservice.exception.UserNotFoundException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), "USER_NOT_FOUND", req);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(EmailAlreadyExistsException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Duplicate Email", ex.getMessage(), "EMAIL_ALREADY_EXISTS", req);
    }

    // На случай, если где-то забудешь обернуть в EmailAlreadyExistsException.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        // Можно сделать умнее: различать constraint name (uk_users_email) и т.п.
        log.warn("Data integrity violation: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Data Integrity Violation", "Constraint violated", "DATA_INTEGRITY_VIOLATION", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Validation Error", message, "VALIDATION_ERROR", req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Constraint Violation", message, "CONSTRAINT_VIOLATION", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        // FIX: без Enum::name (IDE иногда ругается на reference), делаем явно через toString()
        String allowed = ex.getSupportedHttpMethods() == null ? "" :
                ex.getSupportedHttpMethods().stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining(", "));

        String message = allowed.isBlank()
                ? "Method not allowed"
                : "Allowed methods: " + allowed;

        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", message, "METHOD_NOT_ALLOWED", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Unexpected error occurred", "INTERNAL_ERROR", req);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message, String code, HttpServletRequest req) {
        ApiError body = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                error,
                message,
                code,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
