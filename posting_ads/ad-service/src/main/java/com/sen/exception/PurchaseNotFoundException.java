package com.sen.exception;

import java.util.UUID;

public class PurchaseNotFoundException extends RuntimeException {
    public PurchaseNotFoundException(UUID id) {
        super("Покупка не найдена: " + id);
    }
}
