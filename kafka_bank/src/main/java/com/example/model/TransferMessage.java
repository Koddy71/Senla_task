package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public final class TransferMessage {
    private final String id;
    private final Long fromAccountId;
    private final Long toAccountId;
    private final BigDecimal amount;

    @JsonCreator
    public TransferMessage(
            @JsonProperty("id") String id,
            @JsonProperty("fromAccountId") Long fromAccountId,
            @JsonProperty("toAccountId") Long toAccountId,
            @JsonProperty("amount") BigDecimal amount) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}