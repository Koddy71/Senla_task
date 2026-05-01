package com.sen.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sen.dto.request.PaymentCreateRequest;
import com.sen.dto.response.PaymentResponse;
import com.sen.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentCreateRequest request) {
        logger.info("Запрос на создание платежа от пользователя: {}", userDetails.getUsername());
        PaymentResponse response = paymentService.createPayment(userDetails.getUsername(), request);
        logger.info("Платёж успешно создан. ID платежа: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable UUID id) {
        logger.info("Запрос на обработку платежа с ID: {}", id);
        PaymentResponse response = paymentService.processPayment(id);
        logger.info("Платёж с ID {} успешно обработан. Статус: {}", id, response.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Запрос списка транзакций пользователя: {}", userDetails.getUsername());
        List<PaymentResponse> transactions = paymentService.getUserTransactions(userDetails.getUsername());
        logger.info("Найдено {} транзакций для пользователя {}", transactions.size(), userDetails.getUsername());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/ad/{adId}")
    public ResponseEntity<List<PaymentResponse>> getTransactionsByAd(@PathVariable UUID adId) {
        logger.info("Запрос транзакций по объявлению с ID: {}", adId);
        List<PaymentResponse> transactions = paymentService.getTransactionsByAdId(adId);
        logger.info("Найдено {} транзакций для объявления {}", transactions.size(), adId);
        return ResponseEntity.ok(transactions);
    }
}