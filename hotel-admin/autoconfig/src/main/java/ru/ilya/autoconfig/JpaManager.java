package ru.ilya.autoconfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class JpaManager {
    private static final Logger logger = LoggerFactory.getLogger(JpaManager.class);
    private static final String CONFIG_PATH = "core/src/main/resources/config.properties";

    private static SessionFactory sessionFactory;

    public static EntityManager createEntityManager() {
        return getSessionFactory().createEntityManager();
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            init();
        }
        return sessionFactory;
    }

    private static void init(){
        try{
            Properties props = new Properties();
            try(FileReader reader = new FileReader(CONFIG_PATH)){
                props.load(reader);
            }
            Configuration cfg = new Configuration();

            cfg.setProperty(Environment.DRIVER, props.getProperty("db.driver"));
            cfg.setProperty(Environment.URL, props.getProperty("db.url"));
            cfg.setProperty(Environment.USER, props.getProperty("db.user"));
            cfg.setProperty(Environment.PASS, props.getProperty("db.password"));

            cfg.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL95Dialect");
            cfg.setProperty(Environment.SHOW_SQL, "false");     //на лекции сказали полезная настройка, пока отключил
            cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            cfg.setProperty("hibernate.packagesToScan", "ru.ilya.model");

            sessionFactory = cfg.buildSessionFactory();
        } catch (IOException e){
            logger.error("Ошибка чтения конфигурации для JPA", e);
            throw new RuntimeException(e);
        } catch (Exception e){
            logger.error("Ошибка инициализации Hibernate", e);
            throw new RuntimeException(e);
        }

    }
}
