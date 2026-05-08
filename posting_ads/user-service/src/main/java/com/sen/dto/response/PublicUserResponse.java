package com.sen.dto.response;

import java.time.LocalDateTime;

public class PublicUserResponse {
    private String login;
    private String fullname;
    private LocalDateTime createdAt;

    public PublicUserResponse() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}