package com.sen.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sen.service.AdService;

@RestController
@RequestMapping("/internal/ads")
public class InternalAdController {
    private final AdService adService;

    public InternalAdController(AdService adService) {
        this.adService = adService;
    }

    @PostMapping("/{id}/promote")
    public ResponseEntity<Void> promote(@PathVariable UUID id,
            @RequestParam int hours) {
        adService.promoteAd(id, hours);
        return ResponseEntity.ok().build();
    }
}
