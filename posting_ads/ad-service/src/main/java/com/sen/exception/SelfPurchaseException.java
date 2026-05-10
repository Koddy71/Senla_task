package com.sen.exception;

public class SelfPurchaseException extends RuntimeException {
    public SelfPurchaseException() {
        super("Невозможно купить собственное объявление");
    }
}
