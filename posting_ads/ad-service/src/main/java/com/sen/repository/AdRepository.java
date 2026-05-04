package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.Ad;
import com.sen.enums.AdStatus;
//todo @repository не помешает
public interface AdRepository {
    Optional<Ad> findById(UUID id);

    Ad save(Ad ad);

    List<Ad> findBySellerId(UUID sellerId);

    List<Ad> findBySellerIdAndStatus(UUID sellerId, AdStatus status);

}
