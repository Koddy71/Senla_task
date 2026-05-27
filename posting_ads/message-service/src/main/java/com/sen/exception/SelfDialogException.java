package com.sen.exception;

public class SelfDialogException extends RuntimeException {
    public SelfDialogException() {
        super("Нельзя создать диалог с самим собой");
    }

    public SelfDialogException(String message) {
        super(message);
    }
}