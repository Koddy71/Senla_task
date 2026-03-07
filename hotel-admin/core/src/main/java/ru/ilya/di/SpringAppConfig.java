package ru.ilya.di;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ilya.autoconfig.JdbcManager;
import ru.ilya.autoconfig.JpaManager;
import ru.ilya.exceptions.PersistenceException;

@Configuration
@ComponentScan(basePackages = "ru.ilya")
@EnableTransactionManagement
public class SpringAppConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringAppConfig.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setLocation(new ClassPathResource("config.properties"));
        return pspc;
    }

    @Bean
    public DataSource dataSource(
            @Value("${db.driver}") String driverClassName,
            @Value("${db.url}") String url,
            @Value("${db.user}") String username,
            @Value("${db.password}") String password) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        logger.info("DataSource created: url={}, user={}", url, username);
        return ds;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        EntityManager em = null;
        try {
            em = JpaManager.createEntityManager();
            EntityManagerFactory emf = em.getEntityManagerFactory();
            if (emf == null) {
                throw new IllegalStateException("JpaManager не вернул EntityManagerFactory (emf == null)");
            }
            JpaTransactionManager tm = new JpaTransactionManager(emf);
            logger.info("JpaTransactionManager создан на основе EntityManagerFactory из JpaManager");
            return tm;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Bean
    public Object liquibaseRunner(JdbcManager jdbcManager, DataSource dataSource) {
        try (Connection conn = getConnectionForLiquibase(jdbcManager, dataSource)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("db/changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
            logger.info("Liquibase: миграции applied successfully");
        } catch (LiquibaseException le) {
            logger.error("Liquibase error", le);
            throw new PersistenceException("Liquibase failed", le);
        } catch (Exception e) {
            logger.error("Liquibase runner error", e);
            throw new PersistenceException("Liquibase failed", e);
        }
        return new Object();
    }

    private Connection getConnectionForLiquibase(JdbcManager jdbcManager, DataSource ds) throws Exception {
        try {
            Connection c = jdbcManager.getConnection();
            if (c != null)
                return c;
        } catch (Throwable t) {
            logger.warn("JdbcManager.getConnection() failed, fallback to DataSource", t);
        }
        return ds.getConnection();
    }
}