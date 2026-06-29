package com.sen.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sen.entity.Payment;
import com.sen.repository.PaymentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Payment> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Payment.class, id));
    }

    @Override
    public List<Payment> findUserId(UUID userId) {
        TypedQuery<Payment> query = entityManager.createQuery("SELECT pt FROM Payment pt WHERE pt.user.id = :userId ORDER BY pt.createdAt DESC", Payment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Payment> findAdId(UUID adId) {
        TypedQuery<Payment> query = entityManager.createQuery(
                "SELECT pt FROM Payment pt WHERE pt.adId = :adId ORDER BY pt.createdAt DESC",
                Payment.class);
        query.setParameter("adId", adId);
        return query.getResultList();
    }

    @Override
    public Payment save(Payment payment) {
        if(payment.getId()==null){
            entityManager.persist(payment);
            return payment;
        } else {
            return entityManager.merge(payment);
        }
    }
    
}
