package com.sen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageRequest {
    @NotBlank(message = "Текст сообщения обязателен")
    @Size(max = 5000, message = "Текст сообщения не должен содержать более 5000 символов")
    private String text;

    public MessageRequest() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}