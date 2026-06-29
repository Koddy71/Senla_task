package com.sen.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sen.entity.Message;
import com.sen.repository.MessageRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class MessageRepositoryImpl implements MessageRepository{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(em.find(Message.class, id));
    }

    @Override
    public List<Message> findByDialogId(UUID dialogId) {
        TypedQuery<Message> q = em.createQuery(
                "SELECT m FROM Message m WHERE m.dialogId = :dialogId ORDER BY m.sentAt DESC",
                Message.class);
        q.setParameter("dialogId", dialogId);
        return q.getResultList();
    }

    @Override
    public Message save(Message message) {
        if (message.getId() == null) {
            em.persist(message);
            return message;
        }
        return em.merge(message);
    }

    @Override
    public boolean deleteById(UUID id) {
        int deleted = em.createQuery("DELETE FROM Message WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
        return deleted > 0;
    }
    
}
