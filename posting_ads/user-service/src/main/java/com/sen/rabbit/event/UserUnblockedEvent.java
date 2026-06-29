package com.sen.rabbit.event;

import java.util.UUID;

public class UserUnblockedEvent {

    private UUID userId;
    private String login;

    public UserUnblockedEvent() {
    }

    public UserUnblockedEvent(UUID userId, String login) {
        this.userId = userId;
        this.login = login;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}