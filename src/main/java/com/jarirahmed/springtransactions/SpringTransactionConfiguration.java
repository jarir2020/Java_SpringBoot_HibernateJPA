package com.jarirahmed.springtransactions;

import com.jarirahmed.jpa.JpaLesson;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Explicit Spring configuration keeps transaction infrastructure visible
 * before Spring Boot or Spring Data JPA hides it behind auto-configuration.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackageClasses = SpringTransactionService.class)
public class SpringTransactionConfiguration {
    @Bean(destroyMethod = "close")
    public EntityManagerFactory entityManagerFactory(
            @Value("${phase8.database-name:phase8-default}") String databaseName) {
        return JpaLesson.buildEntityManagerFactory(databaseName);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        // Standard JPA leaves isolation application-specific. Hibernate's
        // dialect lets Spring apply the requested JDBC isolation level here.
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
        transactionManager.setJpaDialect(new HibernateJpaDialect());
        return transactionManager;
    }

    @Bean
    public static PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }
}
