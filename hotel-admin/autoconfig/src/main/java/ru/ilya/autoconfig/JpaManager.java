package ru.ilya.autoconfig;

import java.io.FileReader;
import java.io.IOException;
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
    private static final String CONFIG_PATH = "core/src/main/resources/config.properties";
    private static final String PERSISTENCE_UNIT = "hotelPU";

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    private static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            init();
        }
        return entityManagerFactory;
    }

    private static void init() {
        try {
            Properties props = new Properties();
            try (FileReader reader = new FileReader(CONFIG_PATH)) {
                props.load(reader);
            }

            Map<String, String> jpaProps = new HashMap<>();

            jpaProps.put("javax.persistence.jdbc.driver", props.getProperty("db.driver"));
            jpaProps.put("javax.persistence.jdbc.url", props.getProperty("db.url"));
            jpaProps.put("javax.persistence.jdbc.user", props.getProperty("db.user"));
            jpaProps.put("javax.persistence.jdbc.password", props.getProperty("db.password"));

            jpaProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
            jpaProps.put("hibernate.show_sql", "false");

            entityManagerFactory =
                    Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, jpaProps);

        } catch (IOException e) {
            logger.error("Ошибка чтения конфигурации для JPA: {}", CONFIG_PATH , e);
            throw new RuntimeException("Не удалось загрузить файл конфигурации: " + CONFIG_PATH, e);
        } catch (Exception e) {
            logger.error("Ошибка инициализации Hibernate", e);
            throw new RuntimeException("Ошибка инициализации Hibernate", e);
        }
    }
}
