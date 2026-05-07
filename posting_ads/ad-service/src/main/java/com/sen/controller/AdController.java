package com.sen.controller;

import java.util.List;
import java.util.UUID;

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
    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @PostMapping
    public ResponseEntity<AdDetailResponse> create(@AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AdCreateRequest request) {
        return new ResponseEntity<>(adService.createAd(user.getUsername(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdDetailResponse> update(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AdUpdateRequest request) {
        return ResponseEntity.ok(adService.updateAd(id, user.getUsername(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        adService.deleteAd(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<AdDetailResponse>> getMyAds(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(adService.getMyAds(user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<AdResponse>> search(AdFilterRequest filter) {
        return ResponseEntity.ok(adService.searchAds(filter));
    }

    @GetMapping("/seller/{login}")
    public ResponseEntity<List<AdResponse>> getBySeller(@PathVariable String login) {
        return ResponseEntity.ok(adService.getAdsBySeller(login));
    }

    //ADMIN | MANAGER

    @PostMapping("/manager/seller/{id}/block")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> block(@PathVariable UUID id) {
        adService.blockAd(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/manager/seller/{id}/unblock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> unblock(@PathVariable UUID id) {
        adService.unblockAd(id);
        return ResponseEntity.noContent().build();
    }
}
