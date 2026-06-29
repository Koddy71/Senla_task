package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.Payment;

public interface PaymentRepository {
    Optional<Payment> findById(UUID id);
    
    List<Payment> findUserId(UUID userId);

    List<Payment> findAdId(UUID adId);

    Payment save(Payment payment);
    
}
