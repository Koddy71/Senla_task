package ru.ilya.exceptions;

public class EntityException extends ApplicationException {
    public EntityException(String message) {
        super(message);
    }

    public EntityException(String message, Throwable cause) {
        super(message, cause);
    }
}