package com.sen.rabbit.event;

import java.util.UUID;

public class AdPromotionRequestedEvent {

    private UUID paymentId;
    private UUID adId;
    private Integer hours;
    private String userLogin;

    public AdPromotionRequestedEvent() {
    }

    public AdPromotionRequestedEvent(UUID paymentId, UUID adId, Integer hours, String userLogin) {
        this.paymentId = paymentId;
        this.adId = adId;
        this.hours = hours;
        this.userLogin = userLogin;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
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

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}