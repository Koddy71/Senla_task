package com.sen.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class DialogResponse {
    private UUID id;
    private String userLogin1;
    private String userLogin2;
    private LocalDateTime createdAt;

    public DialogResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserLogin1() {
        return userLogin1;
    }

    public void setUserLogin1(String userLogin1) {
        this.userLogin1 = userLogin1;
    }

    public String getUserLogin2() {
        return userLogin2;
    }

    public void setUserLogin2(String userLogin2) {
        this.userLogin2 = userLogin2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}