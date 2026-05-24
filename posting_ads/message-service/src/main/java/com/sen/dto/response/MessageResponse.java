package com.sen.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageResponse {
    private UUID id;
    private UUID dialogId;
    private String senderLogin;
    private String text;
    private LocalDateTime sentAt;
    private boolean read;

    public MessageResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDialogId() {
        return dialogId;
    }

    public void setDialogId(UUID dialogId) {
        this.dialogId = dialogId;
    }

    public String getSender() {
        return senderLogin;
    }

    public void setSender(String senderLogin) {
        this.senderLogin = senderLogin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}