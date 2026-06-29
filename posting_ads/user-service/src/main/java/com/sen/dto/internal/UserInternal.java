package com.sen.dto.internal;

import java.util.UUID;

/**
 * Используется когда Ad Service или Messaging Service запрашивает
 * информацию о пользователе через REST
 */

public class UserInternal {
    private UUID id;
    private String login;
    private String fullname;
    private String role;
    private Boolean blocked;

    public UserInternal() {
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

}