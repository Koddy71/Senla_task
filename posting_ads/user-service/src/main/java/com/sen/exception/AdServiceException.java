package com.sen.exception;

public class AdServiceException extends RuntimeException {
    public AdServiceException(String message) {
        super(message);
    }

    public AdServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
