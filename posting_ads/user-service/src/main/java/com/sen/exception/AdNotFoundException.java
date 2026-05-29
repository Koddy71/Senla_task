package com.sen.exception;

public class AdNotFoundException extends RuntimeException {
    public AdNotFoundException(String message) {
        super(message);
    }
}
