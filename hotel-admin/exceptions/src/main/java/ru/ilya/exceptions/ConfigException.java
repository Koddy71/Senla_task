package ru.ilya.exceptions;

public class ConfigException extends ApplicationException {
    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}