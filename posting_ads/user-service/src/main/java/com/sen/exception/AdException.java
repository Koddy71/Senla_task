package com.sen.exception;

public class AdException extends RuntimeException {
    public AdException() {
        super("Объявление не активно и не может быть продвинуто");
    }
}
