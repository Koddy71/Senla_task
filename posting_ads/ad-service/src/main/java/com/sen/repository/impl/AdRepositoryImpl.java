package com.sen.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sen.dto.request.AdFilterRequest;
import com.sen.entity.Ad;
import com.sen.enums.AdStatus;
import com.sen.repository.AdRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@Repository
public class AdRepositoryImpl implements AdRepository{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Ad> findById(UUID id) {
        return Optional.ofNullable(em.find(Ad.class, id));
    }

    @Override
    public Ad save(Ad ad) {
        if (ad.getId() == null) {
            em.persist(ad);
            return ad;
        }
        return em.merge(ad);
    }

    @Override
    public List<Ad> findBySellerId(UUID sellerId) {
        TypedQuery<Ad> q = em.createQuery(
            "SELECT a FROM Ad a WHERE a.sellerId = :sellerId ORDER BY a.createdAt DESC", Ad.class);
        q.setParameter("sellerId", sellerId);
        return q.getResultList();
    }

    @Override
    public List<Ad> findBySellerIdAndStatus(UUID sellerId, AdStatus status) {
        TypedQuery<Ad> q = em.createQuery(
                "SELECT a FROM Ad a WHERE a.sellerId = :sellerId AND a.status = :status ORDER BY a.createdAt DESC",
                Ad.class);
        q.setParameter("sellerId", sellerId);
        q.setParameter("status", status);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")  // отключаем предупреждение, возвращаем Query (сырой тип)
    @Override
    public List<Ad> searchAds(AdFilterRequest filter) {
        //вместо продавца, подставляет его средний рейтинг
        String sql = """            
                SELECT a.* FROM ad_service.ad a         
                LEFT JOIN (
                    SELECT a2.seller_id, AVG(p.score) as avg_score
                    FROM ad_service.ad a2
                    JOIN ad_service.purchase p ON p.ad_id = a2.id
                    WHERE p.score IS NOT NULL
                    GROUP BY a2.seller_id
                ) r ON r.seller_id = a.seller_id
                WHERE a.status = 'ACTIVE'
                """;

        Map<String, Object> params = new HashMap<>();

        if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
            sql += " AND a.category = :category";
            params.put("category", filter.getCategory());
        }
        if (filter.getMinPrice() != null) {
            sql += " AND a.price >= :minPrice";
            params.put("minPrice", filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            sql += " AND a.price <= :maxPrice";
            params.put("maxPrice", filter.getMaxPrice());
        }
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            sql += " AND a.title ILIKE :search";
            params.put("search", "%" + filter.getSearch() + "%");
        }

        //ORDER BY сортирует по возрастанию, поэтому везде DESC
        sql += """
                 ORDER BY
                    CASE WHEN a.promoted_until > NOW() THEN 1 ELSE 0 END DESC,  
                    COALESCE(r.avg_score, 0) DESC,
                    a.created_at DESC
                LIMIT :limit OFFSET :offset
                """;
        params.put("limit", filter.getSize());
        params.put("offset", (filter.getPage() - 1) * filter.getSize());

        Query q = em.createNativeQuery(sql, Ad.class);
        params.forEach(q::setParameter);
        return q.getResultList();
    }
    
}
