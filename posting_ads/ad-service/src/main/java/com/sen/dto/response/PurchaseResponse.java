package com.sen.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sen.enums.PurchaseStatus;

public class PurchaseResponse {
    private UUID id;
    private UUID adId;
    private String buyerLogin;
    private PurchaseStatus status;
    private LocalDateTime completedAt;
    private Integer score;
    private String comment;
    private LocalDateTime reviewCreatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAdId() {
        return adId;
    }

    public void setAdId(UUID adId) {
        this.adId = adId;
    }

    public String getBuyerLogin() {
        return buyerLogin;
    }

    public void setBuyerLogin(String buyerLogin) {
        this.buyerLogin = buyerLogin;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewCreatedAt() {
        return reviewCreatedAt;
    }

    public void setReviewCreatedAt(LocalDateTime reviewCreatedAt) {
        this.reviewCreatedAt = reviewCreatedAt;
    }
}
