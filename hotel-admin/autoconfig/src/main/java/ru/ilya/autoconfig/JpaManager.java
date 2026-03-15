package ru.ilya.autoconfig;

import ru.ilya.exceptions.ConfigException;
import ru.ilya.exceptions.PersistenceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaManager {

    private static final Logger logger = LoggerFactory.getLogger(JpaManager.class);
    private static final String PERSISTENCE_UNIT = "hotelPU";

    private static final JpaManager INSTANCE = new JpaManager();

    private volatile EntityManagerFactory entityManagerFactory;
    private final Object initLock = new Object();

    private JpaManager() {
    }

    public static EntityManager createEntityManager() {
        try {
            INSTANCE.ensureInitialized();
            return INSTANCE.entityManagerFactory.createEntityManager();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать EntityManager: " + e.getMessage(), e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        INSTANCE.ensureInitialized();
        return INSTANCE.entityManagerFactory;
    }

    // лениво инициализируем EMF
    private void ensureInitialized() {
        if (entityManagerFactory == null) {
            synchronized (initLock) {
                if (entityManagerFactory == null) {
                    initInternal();
                }
            }
        }
    }

    // читает config.properties и создает EMF
    private void initInternal() {
        logger.info("Инициализация JPA EntityManagerFactory (lazy)");
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new ConfigException("config.properties not found in classpath");
            }
            Properties props = new Properties();
            props.load(is);

            Map<String, String> jpaProps = new HashMap<>();
            jpaProps.put("javax.persistence.jdbc.url", props.getProperty("db.url"));
            jpaProps.put("javax.persistence.jdbc.driver", props.getProperty("db.driver"));
            jpaProps.put("javax.persistence.jdbc.user", props.getProperty("db.user"));
            jpaProps.put("javax.persistence.jdbc.password", props.getProperty("db.password"));
            jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
            jpaProps.put("hibernate.show_sql", props.getProperty("hibernate.show_sql", "false"));

            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, jpaProps);
            logger.info("EntityManagerFactory создан успешно.");
        } catch (IOException e) {
            logger.error("Ошибка чтения конфигурации для JPA", e);
            throw new RuntimeException(
                    new ConfigException("Не удалось загрузить файл конфигурации config.properties", e));
        } catch (Exception e) {
            logger.error("Ошибка инициализации Hibernate", e);
            throw new RuntimeException(new PersistenceException("Ошибка инициализации Hibernate", e));
        }
    }

}