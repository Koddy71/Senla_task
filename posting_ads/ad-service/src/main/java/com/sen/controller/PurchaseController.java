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

import com.sen.dto.request.ReviewRequest;
import com.sen.dto.response.PurchaseResponse;
import com.sen.dto.response.SellerRatingResponse;
import com.sen.service.PurchaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/ads/{adId}")
    public ResponseEntity<PurchaseResponse> create(@PathVariable UUID adId,
            @AuthenticationPrincipal UserDetails user) {
        logger.info("Входящий запрос на покупку объявления {} от пользователя {}", adId, user.getUsername());
        PurchaseResponse response = purchaseService.createPurchase(adId, user.getUsername());
        logger.info("Покупка объявления {} успешно создана, id покупки: {}", adId, response.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<PurchaseResponse> addReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ReviewRequest request) {
        logger.info("Входящий запрос на добавление отзыва к покупке {} от пользователя {}", id, user.getUsername());
        PurchaseResponse response = purchaseService.addReview(id, user.getUsername(), request);
        logger.info("Отзыв к покупке {} успешно добавлен", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PurchaseResponse>> getMyPurchases(
            @AuthenticationPrincipal UserDetails user) {
        logger.info("Запрос списка покупок текущего пользователя: {}", user.getUsername());
        List<PurchaseResponse> responses = purchaseService.getMyPurchases(user.getUsername());
        logger.info("Получено {} покупок для пользователя {}", responses.size(), user.getUsername());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getById(@PathVariable UUID id) {
        logger.info("Запрос покупки по id: {}", id);
        PurchaseResponse response = purchaseService.getPurchaseById(id);
        logger.info("Покупка с id {} успешно получена", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{login}/rating")
    public ResponseEntity<SellerRatingResponse> getRating(@PathVariable String login) {
        logger.info("Запрос рейтинга продавца: {}", login);
        SellerRatingResponse response = purchaseService.getSellerRating(login);
        logger.info("Рейтинг продавца {}: средний балл {}, количество отзывов {}",
                login, response.getAverageRating(), response.getTotalReviews());
        return ResponseEntity.ok(response);
    }
}