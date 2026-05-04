package com.sen.service;

import java.util.List;
import java.util.UUID;

import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdFilterRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;

public interface AdService {

    // myLogin берётся из JWT (login).
    // Перед сохранением проверяем пользователя в User Service (существует / не заблокирован)
    AdDetailResponse createAd(String myLogin, AdCreateRequest request);

    // Редактирование своего объявления. Проверка владельца внутри.
    AdDetailResponse updateAd(UUID adId, String myLogin, AdUpdateRequest request);

    // (Смена статуса на ARCHIVED). Только владелец или менеджер/админ
    void deleteAd(UUID adId, String myLogin);
    
    // Все мои активные объявления
    List<AdDetailResponse> getMyAds(String myLogin);

    /**
     * Поиск с фильтрами + сортировка:
     * 1. promoted_until > now (топ)
     * 2. рейтинг продавца (высокий -> низкий)
     * 3. created_at DESC
     */
    List<AdResponse> searchAds(AdFilterRequest filter);

    //Все активные объявления конкретного продавца (по логину).
    List<AdResponse> getAdsBySeller(String sellerLogin);

    /**
     * Активация продвижения. Вызывается из User Service (internal) после успешной
     * оплаты.
     * Устанавливает promoted_until = now + hours.
     */
    void promoteAd(UUID adId, int hours);

    // ADMIN|MANAGER
    void blockAds(String sellerLogin);

    void unblockAds(String sellerLogin);
}
