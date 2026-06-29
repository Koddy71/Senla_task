package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.Purchase;

public interface PurchaseRepository {
    Optional<Purchase> findById(UUID id);
    
    Purchase save(Purchase purchase);

    List<Purchase> findByBuyerId(UUID buerId);

    // Optional<Purchase> findByAdId(UUID adId);

    SellerRatingProjection getSellerRating(UUID sellerId);
}
