package com.sen.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
public class LiquibaseConfig {
    @Value("${liquibase.changeLog}")
    private String changelog;

    @Value("${hibernate.default_schema}")
    private String dbSchema;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changelog);
        liquibase.setDefaultSchema(dbSchema);
        liquibase.setShouldRun(true);
        return liquibase;
    }
}
