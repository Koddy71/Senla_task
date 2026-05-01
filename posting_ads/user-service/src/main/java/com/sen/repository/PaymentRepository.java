package com.sen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sen.entity.Payment;
import com.sen.enums.PaymentStatus;

public interface PaymentRepository {
    Optional<Payment> findById(UUID id);
    
    List<Payment> findUserId(UUID userId);

    List<Payment> findAdId(UUID adId);

    Payment save(Payment payment);
    
    void uodateStatus(UUID id, PaymentStatus status);
}
