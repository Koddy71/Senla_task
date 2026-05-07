package com.sen.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sen.entity.User;
import com.sen.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        List<User> list = em.createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    public List<User> findAll(int page, int size) {
        return em.createQuery("select u from User u order by u.id desc", User.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        }
        return em.merge(user);
    }

    @Override
    public void delete(User user) {
        if (em.contains(user)) {
            em.remove(user);
        } else {
            em.remove(em.merge(user));
        }
    }

    @Override
    public boolean existsByLogin(String login) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class);
        query.setParameter("login", login);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<User> findAllById(List<UUID> ids) {
        return em.createQuery("select u from User u where u.id in :ids", User.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
