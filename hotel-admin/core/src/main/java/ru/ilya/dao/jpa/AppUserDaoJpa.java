package ru.ilya.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ru.ilya.autoconfig.JpaManager;
import ru.ilya.dao.GenericDao;
import ru.ilya.exceptions.PersistenceException;
import ru.ilya.model.AppUser;

@Repository
public class AppUserDaoJpa implements GenericDao<AppUser, Integer>{
    private static final Logger logger= LoggerFactory.getLogger(AppUserDaoJpa.class);

    private static final String FIND_BY_LOGIN = "SELECT u FROM AppUser u WHERE u.login = :login";
    private static final String FIND_ALL = "SELECT u FROM AppUser u";
    public AppUserDaoJpa(){
    }

    public AppUser findByLogin(String login){
        EntityManager em = JpaManager.createEntityManager();
        try{
            TypedQuery<AppUser> q = em.createQuery(FIND_BY_LOGIN, AppUser.class);
            q.setParameter("login", login);
            List<AppUser> result = q.getResultList();
            if (!result.isEmpty()){
                return result.get(0);
            }
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public AppUser create(AppUser model){
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            em.persist(model);
            tx.commit();
            return model;
        } catch (Exception e){
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при создании пользователя {}", model.getLogin(), e);
            throw new PersistenceException("Ошибка вставки пользователя в БД", e);
        } finally {
            em.close();
        }
    }

    @Override
    public AppUser findById(Integer id){
        EntityManager em = JpaManager.createEntityManager();
        try{
            return em.find(AppUser.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<AppUser> findAll(){
        EntityManager em = JpaManager.createEntityManager();
        try{
            TypedQuery<AppUser> q = em.createQuery(FIND_ALL, AppUser.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public AppUser update(AppUser model) {
        EntityManager em = JpaManager.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AppUser merged = em.merge(model);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при обновлении пользователя {}", model.getLogin(), e);
            throw new PersistenceException("Ошибка обновления пользователя в БД", e);
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
            AppUser u = em.find(AppUser.class, id);
            if (u != null) {
                em.remove(u);
                tx.commit();
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при удалении пользователя с id={}", id, e);
            throw new PersistenceException("Ошибка удаления пользователя из БД", e);
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
            em.createQuery("DELETE FROM AppUser").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            logger.error("Ошибка при удалении всех пользователей", e);
            throw new PersistenceException("Ошибка очистки таблицы app_user", e);
        } finally {
            em.close();
        }
    }
}
