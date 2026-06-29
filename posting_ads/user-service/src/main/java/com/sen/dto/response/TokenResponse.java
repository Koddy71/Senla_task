package com.sen.dto.response;

public class TokenResponse {
    private String token;
    private String type = "Bearer";
    // private Long expiresIn;

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // public Long getExpiresIn() {
    //     return expiresIn;
    // }

    // public void setExpiresIn(Long expiresIn) {
    //     this.expiresIn = expiresIn;
    // }
}