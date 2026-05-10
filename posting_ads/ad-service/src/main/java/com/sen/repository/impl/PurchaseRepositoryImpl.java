package com.sen.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sen.entity.Purchase;
import com.sen.repository.PurchaseRepository;
import com.sen.repository.SellerRatingProjection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class PurchaseRepositoryImpl implements PurchaseRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Purchase> findById(UUID id) {
        return Optional.ofNullable(em.find(Purchase.class, id));
    }

    @Override
    public Purchase save(Purchase purchase) {
        if (purchase.getId() == null) {
            em.persist(purchase);
            return purchase;
        }
        return em.merge(purchase);
    }

    @Override
    public List<Purchase> findByBuyerId(UUID buerId) {
        TypedQuery<Purchase> q = em.createQuery(
            "SELECT p FROM Purchase p JOIN FETCH p.ad WHERE p.buyerId = :buyerId ORDER BY p.completedAt DESC", 
            Purchase.class)
            .setParameter("buyerId", buerId);
        return q.getResultList();
    }

    @Override
    public SellerRatingProjection getSellerRating(UUID sellerId) {
        return em.createQuery("""
                SELECT new com.example.SellerRatingProjection(
                    COALESCE(AVG(p.score), 0.0),
                    COUNT(p.score)
                )
                FROM Purchase p
                JOIN p.ad a
                WHERE a.sellerId = :sellerId AND p.score IS NOT NULL
                """, SellerRatingProjection.class)
                .setParameter("sellerId", sellerId)
                .getSingleResult();
    }
}

