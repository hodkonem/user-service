package ru.itwizardry.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("User with email already exists: " + email);
    }

    public EmailAlreadyExistsException(String email, Throwable cause) {
        super("User with email already exists: " + email, cause);
    }
}
