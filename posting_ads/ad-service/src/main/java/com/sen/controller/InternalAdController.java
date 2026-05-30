package com.sen.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sen.dto.internal.AdInternal;
import com.sen.service.AdService;

@RestController
@RequestMapping("/internal/ads")
public class InternalAdController {

    private static final Logger logger = LoggerFactory.getLogger(InternalAdController.class);

    private final AdService adService;

    public InternalAdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdInternal> getAdById(@PathVariable UUID id) {
        logger.debug("Внутренний запрос на получение объявления id: {}", id);
        AdInternal ad = adService.getAdById(id);
        logger.debug("Внутренний запрос выполнен успешно для объявления id: {}", id);
        return ResponseEntity.ok(ad);
    }
}