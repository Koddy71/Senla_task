package ru.ilya.exceptions;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}