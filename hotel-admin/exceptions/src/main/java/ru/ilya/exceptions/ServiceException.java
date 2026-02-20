package ru.ilya.exceptions;

public class ServiceException extends EntityException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}