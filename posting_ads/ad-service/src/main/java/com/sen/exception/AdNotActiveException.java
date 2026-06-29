package com.sen.exception;

public class AdNotActiveException extends RuntimeException {
    public AdNotActiveException() {
        super("Объявление не активно");
    }
}