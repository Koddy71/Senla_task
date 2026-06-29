package com.sen.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PaymentCreateRequest {

    @NotNull(message = "Ad ID is required")
    private UUID adId;

    @NotNull(message = "Hours is required")
    @Min(value = 1, message = "Minimum 1 hour")
    private Integer hours;

    public PaymentCreateRequest() {
    }

    public UUID getAdId() {
        return adId;
    }

    public void setAdId(UUID adId) {
        this.adId = adId;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}