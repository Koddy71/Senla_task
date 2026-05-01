package com.sen.dto.internal;

import java.util.UUID;

import com.sen.enums.Role;

/**
 * Используется когда Ad Service или Messaging Service запрашивает
 * информацию о пользователе через REST
 */

public class UserInternal {
    private UUID id;
    private String login;
    private String fullname;
    private Role role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

}