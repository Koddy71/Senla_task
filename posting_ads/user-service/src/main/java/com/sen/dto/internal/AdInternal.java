package com.sen.dto.internal;

import java.math.BigDecimal;
import java.util.UUID;

public class AdInternal {
    private UUID id;
    private UUID sellerId;
    private BigDecimal price;
    private String status;
    private boolean promoted;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }
}
