package com.sen.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sen.dto.request.AdCreateRequest;
import com.sen.dto.request.AdFilterRequest;
import com.sen.dto.request.AdUpdateRequest;
import com.sen.dto.response.AdDetailResponse;
import com.sen.dto.response.AdResponse;
import com.sen.service.AdService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    private static final Logger logger = LoggerFactory.getLogger(AdController.class);

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @PostMapping
    public ResponseEntity<AdDetailResponse> create(@AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AdCreateRequest request) {
        logger.info("Входящий запрос на создание объявления от пользователя: {}", user.getUsername());
        AdDetailResponse response = adService.createAd(user.getUsername(), request);
        logger.info("Объявление успешно создано, id: {}, пользователь: {}", response.getId(), user.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdDetailResponse> update(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AdUpdateRequest request) {
        logger.info("Входящий запрос на обновление объявления id {} от пользователя: {}", id, user.getUsername());
        AdDetailResponse response = adService.updateAd(id, user.getUsername(), request);
        logger.info("Объявление id {} успешно обновлено пользователем: {}", id, user.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        logger.info("Входящий запрос на удаление объявления id {} от пользователя: {}", id, user.getUsername());
        adService.deleteAd(id, user.getUsername());
        logger.info("Объявление id {} успешно удалено (архивировано) пользователем: {}", id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<AdDetailResponse>> getMyAds(
            @AuthenticationPrincipal UserDetails user) {
        logger.info("Запрос списка своих объявлений от пользователя: {}", user.getUsername());
        List<AdDetailResponse> responses = adService.getMyAds(user.getUsername());
        logger.info("Получено {} своих объявлений для пользователя: {}", responses.size(), user.getUsername());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<AdResponse>> search(AdFilterRequest filter) {
        logger.info("Поисковый запрос объявлений с фильтром: {}", filter);
        List<AdResponse> responses = adService.searchAds(filter);
        logger.info("Результат поиска: найдено {} объявлений", responses.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/seller/{login}")
    public ResponseEntity<List<AdResponse>> getBySeller(@PathVariable String login) {
        logger.info("Запрос объявлений продавца с логином: {}", login);
        List<AdResponse> responses = adService.getAdsBySeller(login);
        logger.info("Найдено {} объявлений у продавца {}", responses.size(), login);
        return ResponseEntity.ok(responses);
    }

    // ADMIN | MANAGER

    @PostMapping("/manager/seller/{id}/block")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> block(@PathVariable UUID id) {
        logger.info("Административный/менеджерский запрос на блокировку объявления id: {}", id);
        adService.blockAd(id);
        logger.info("Объявление id {} успешно заблокировано", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/manager/seller/{id}/unblock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> unblock(@PathVariable UUID id) {
        logger.info("Административный/менеджерский запрос на разблокировку объявления id: {}", id);
        adService.unblockAd(id);
        logger.info("Объявление id {} успешно разблокировано", id);
        return ResponseEntity.noContent().build();
    }
}