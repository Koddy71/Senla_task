package com.sen.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sen.entity.Dialog;
import com.sen.repository.DialogRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class DialogRepositoryImpl implements DialogRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Dialog> findById(UUID id) {
        return Optional.ofNullable(em.find(Dialog.class, id));
    }

    @Override
    public Optional<Dialog> findByUserId(UUID userId) {
        TypedQuery<Dialog> q = em.createQuery(
                "SELECT d FROM Dialog d WHERE d.user1Id = :userId OR d.user2Id = :userId",
                Dialog.class);
        q.setParameter("userId", userId);
        return q.getResultStream().findFirst();
    }

    @Override
    public List<Dialog> findDialogsByUserId(UUID userId) {
        TypedQuery<Dialog> q = em.createQuery(
                "SELECT d FROM Dialog d WHERE d.user1Id = :userId OR d.user2Id = :userId ORDER BY d.createdAt DESC",
                Dialog.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public Dialog save(Dialog dialog) {
        if (dialog.getId() == null) {
            em.persist(dialog);
            return dialog;
        }
        return em.merge(dialog);
    }

    @Override
    public void deleteById(UUID id) {
        em.createQuery("DELETE FROM Dialog WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
