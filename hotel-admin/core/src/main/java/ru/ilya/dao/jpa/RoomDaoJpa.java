package ru.ilya.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autoconfig.JpaManager;
import ru.ilya.dao.GenericDao;
import ru.ilya.model.Room;

public class RoomDaoJpa implements GenericDao<Room, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(RoomDaoJpa.class);

    public RoomDaoJpa(){
    }
    
    @Override
    public Room create(Room model){
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(model);
            tx.commit();
            return model;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error creating Room {}", model.getNumber(), e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public Room findById(Integer id) {
        EntityManager em = JpaManager.createEntityManager();
        try {
            return em.find(Room.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Room> findAll() {
        EntityManager em = JpaManager.createEntityManager();
        try {
            TypedQuery<Room> q = em.createQuery("SELECT r FROM Room r", Room.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Room update(Room model) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Room merged = em.merge(model);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Error updating Room {}", model.getNumber(), e);
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
            Room r = em.find(Room.class, id);
            if (r != null) {
                em.remove(r);
                tx.commit();
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Error deleting Room id={}", id, e);
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
            em.createQuery("DELETE FROM Room").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Error deleting all Rooms", e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}
