package com.sen.exception;

public class DialogAlreadyExistsException extends RuntimeException {
    public DialogAlreadyExistsException() {
        super("Диалог уже существует");
    }

    public DialogAlreadyExistsException(String message) {
        super(message);
    }
}