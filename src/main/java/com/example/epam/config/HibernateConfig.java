package com.example.epam.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.epam.dao")
@EnableTransactionManagement
public class HibernateConfig {

    @Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://postgres:5432/training_db", "postgres", "postgres")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .outOfOrder(true)
                .load();
        flyway.migrate();
        return flyway;
    }
}