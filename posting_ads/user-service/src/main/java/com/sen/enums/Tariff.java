package com.sen.enums;

import java.math.BigDecimal;

public enum Tariff {
    HOUR_1(1, new BigDecimal("50.00")),
    HOUR_6(6, new BigDecimal("249.90")),
    HOUR_24(24, new BigDecimal("799.90"));

    private final int hours;
    private final BigDecimal amount;

    Tariff(int hours, BigDecimal amount) {
        this.hours = hours;
        this.amount = amount;
    }

    public int getHours() {
        return hours;
    }

    public java.math.BigDecimal getAmount() {
        return amount;
    }
}
