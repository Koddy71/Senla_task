package ru.ilya.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private String tokenType;
    private String login;
    private String role;
    private List<String> authorities;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tokenType, String login, String role, List<String> authorities) {
        this.token = token;
        this.tokenType = tokenType;
        this.login = login;
        this.role = role;
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}