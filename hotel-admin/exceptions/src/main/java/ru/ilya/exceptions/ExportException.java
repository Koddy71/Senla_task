package ru.ilya.exceptions;

public class ExportException extends ApplicationException {
    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}