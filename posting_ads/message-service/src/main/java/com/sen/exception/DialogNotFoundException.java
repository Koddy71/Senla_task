package com.sen.exception;

import java.util.UUID;

public class DialogNotFoundException extends RuntimeException {
    public DialogNotFoundException(UUID dialogId) {
        super("Диалог не найден: " + dialogId);
    }

    public DialogNotFoundException(String message) {
        super(message);
    }
}