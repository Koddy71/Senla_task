package com.sen.service;

import java.util.List;
import java.util.UUID;

import com.sen.dto.request.ReviewRequest;
import com.sen.dto.response.PurchaseResponse;
import com.sen.dto.response.SellerRatingResponse;

public interface PurchaseService {

    /***
     * - объявление ACTIVE
     * - покупатель != продавец
     * - покупатель не заблокирован
     * После успеха: статус ad -> SOLD, создание записи purchase.
     */
    PurchaseResponse createPurchase(UUID adId, String buyerLogin);

    /**
     * Оставить отзыв (score + comment) по завершённой покупке.
     * Только buyer из этой сделки.
     */
    PurchaseResponse addReview(UUID purchaseId, String buyerLogin, ReviewRequest request);

    // История покупок
    List<PurchaseResponse> getMyPurchases(String buyerLogin);

    // Средний рейтинг и количество отзывов продавца.
    SellerRatingResponse getSellerRating(String sellerLogin);

    // Детали конкретной покупки (для проверки статуса).
    PurchaseResponse getPurchaseById(UUID purchaseId);
}
