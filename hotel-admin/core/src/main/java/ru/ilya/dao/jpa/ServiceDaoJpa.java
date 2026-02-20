package ru.ilya.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.ilya.autoconfig.JpaManager;
import ru.ilya.dao.GenericDao;
import ru.ilya.model.Service;

@Component
public class ServiceDaoJpa implements GenericDao<Service, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDaoJpa.class);

    public ServiceDaoJpa() {
    }

    @Override
    public Service create(Service model) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(model);
            tx.commit();
            return model;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при создании услуги {}", model.getName(), e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public Service findById(Integer id) {
        EntityManager em = JpaManager.createEntityManager();
        try {
            return em.find(Service.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Service> findAll() {
        EntityManager em = JpaManager.createEntityManager();
        try {
            TypedQuery<Service> q = em.createQuery("SELECT s FROM Service s", Service.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Service update(Service model) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Service merged = em.merge(model);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при обновлении услуги {}", model.getName(), e);
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
            Service s = em.find(Service.class, id);
            if (s != null) {
                em.remove(s);
                tx.commit();
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при удалении услуги с id={}", id, e);
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
            em.createQuery("DELETE FROM Service").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при удалении всех услуг", e);
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

}