package ru.ilya.di;

import java.sql.Connection; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.ilya.autoconfig.JdbcManager;
import ru.ilya.exceptions.PersistenceException;

@Configuration
@ComponentScan(basePackages = "ru.ilya")
public class SpringAppConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringAppConfig.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setLocation(new ClassPathResource("config.properties"));
        return pspc;
    }

    @Bean
    public Object liquibaseRunner(JdbcManager jdbcManager) {
        try (Connection conn = jdbcManager.getConnection()){
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("db/changelog/changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
            logger.info("Liquibase: миграции применены успешно");
        } catch (LiquibaseException le) {
            logger.error("Ошибка Liquibase при применении миграций", le);
            throw new PersistenceException("Liquibase завершился с ошибкой", le);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении Liquibase", e);
            throw new PersistenceException("Liquibase завершился с ошибкой", e);
        }
        return new Object();
    }
}
