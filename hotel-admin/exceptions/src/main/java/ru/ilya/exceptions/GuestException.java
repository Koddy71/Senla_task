package ru.ilya.exceptions;

public class GuestException extends EntityException {
    public GuestException(String message) {
        super(message);
    }

    public GuestException(String message, Throwable cause) {
        super(message, cause);
    }
}