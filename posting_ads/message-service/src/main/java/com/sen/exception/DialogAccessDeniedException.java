package com.sen.exception;

public class DialogAccessDeniedException extends RuntimeException {
    public DialogAccessDeniedException() {
        super("У вас нет доступа к этому диалогу");
    }

    public DialogAccessDeniedException(String message) {
        super(message);
    }
}