package ru.ilya.di;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;
import ru.ilya.autoconfig.JdbcManager;
import ru.ilya.autoconfig.JpaManager;

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

    @Bean(name = "liquibase")
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        liquibase.setShouldRun(true);
        logger.info("SpringLiquibase bean configured (changeLog={})", "db/changelog/changelog-master.xml");
        return liquibase;
    }

    @Bean(name = "transactionManager")
    @DependsOn("liquibase")
    public PlatformTransactionManager transactionManager() {
        javax.persistence.EntityManager em = null;
        try {
            em = JpaManager.createEntityManager();
            javax.persistence.EntityManagerFactory emf = em.getEntityManagerFactory();
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
        return new Object();
    }
}