// src/main/java/com/sen/entity/Message.java
package com.sen.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages", schema = "message_service")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dialog_id", nullable = false)
    private UUID dialogId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;


    @PrePersist
    void onCreate() {
        sentAt = LocalDateTime.now();
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

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
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

}