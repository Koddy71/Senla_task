package com.sen.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sen.dto.request.CreateDialogRequest;
import com.sen.dto.request.MessageRequest;
import com.sen.dto.response.DialogResponse;
import com.sen.dto.response.MessageResponse;
import com.sen.service.DialogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dialogs")
public class DialogController {
    private static final Logger logger = LoggerFactory.getLogger(DialogController.class);

    private final DialogService dialogService;

    public DialogController(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @PostMapping
    public ResponseEntity<DialogResponse> createDialog(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateDialogRequest request) {
        logger.info("Запрос на создание диалога от пользователя {}", userDetails.getUsername());
        DialogResponse response = dialogService.createDialog(userDetails.getUsername(), request);
        logger.info("Диалог успешно создан, id: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<DialogResponse>> getMyDialogs(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Запрос списка диалогов пользователя {}", userDetails.getUsername());
        List<DialogResponse> response = dialogService.getMyDialogs(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{dialogId}/messages")
    public ResponseEntity<List<MessageResponse>> getDialog(
            @PathVariable UUID dialogId,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Запрос сообщений диалога {} пользователем {}", dialogId, userDetails.getUsername());
        List<MessageResponse> response = dialogService.getDialog(dialogId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{dialogId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID dialogId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MessageRequest request) {
        logger.info("Отправка сообщения в диалог {} пользователем {}", dialogId, userDetails.getUsername());
        MessageResponse response = dialogService.sendMessage(dialogId, userDetails.getUsername(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{dialogId}")
    public ResponseEntity<Void> deleteDialog(
            @PathVariable UUID dialogId,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Удаление диалога {} пользователем {}", dialogId, userDetails.getUsername());
        dialogService.deleteDialog(dialogId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID messageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Удаление сообщения {} пользователем {}", messageId, userDetails.getUsername());
        dialogService.deleteMessage(messageId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}