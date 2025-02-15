package com.example.epam.config;

import com.example.epam.entity.User;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // Correct Spring import


@Configuration // Correct Spring annotation
public class HibernateConfig {

    @Bean
    public SessionFactory sessionFactory() {
        try {
            return new org.hibernate.cfg.Configuration()
                    .addAnnotatedClass(User.class)
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Hibernate SessionFactory", e);
        }
    }

    @Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://postgres:5432/training_db", "postgres", "postgres")
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
        return flyway;
    }
}
