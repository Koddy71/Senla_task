package com.sen.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class BalanceUpRequest {
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    public BalanceUpRequest(){}

    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }

    public BigDecimal getAmount(){
        return amount;
    }
}
