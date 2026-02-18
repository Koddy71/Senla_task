package ru.ilya.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autoconfig.JpaManager;
import ru.ilya.dao.GenericDao;
import ru.ilya.model.Guest;

public class GuestDaoJpa implements GenericDao<Guest, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(GuestDaoJpa.class);

    public GuestDaoJpa(){
    }

    @Override
    public Guest create(Guest model) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(model);
            tx.commit();
            return model;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error creating Guest {}", model.getName(), e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public Guest findById(Integer id) {
        EntityManager em = JpaManager.createEntityManager();
        try {
            return em.find(Guest.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Guest> findAll() {
        EntityManager em = JpaManager.createEntityManager();
        try {
            TypedQuery<Guest> q = em.createQuery("""
                    SELECT DISTINCT g FROM Guest g 
                    LEFT JOIN FETCH g.services LEFT JOIN FETCH g.room
                    """, Guest.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Guest update(Guest model) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Guest merged = em.merge(model);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error updating Guest {}", model.getName(), e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean delete(Integer id) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Guest g = em.find(Guest.class, id);
            if (g != null) {
                em.remove(g);
                tx.commit();
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error deleting Guest id={}", id, e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM Guest").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error deleting all Guests", e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}
    