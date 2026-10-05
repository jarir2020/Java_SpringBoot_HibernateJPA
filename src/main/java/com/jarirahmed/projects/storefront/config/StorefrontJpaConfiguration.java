package com.jarirahmed.projects.storefront.config;

import com.jarirahmed.projects.storefront.entity.Category;
import com.jarirahmed.projects.storefront.repository.CartRepository;
import com.jarirahmed.projects.storefront.repository.CategoryRepository;
import com.jarirahmed.projects.storefront.repository.OrderRepository;
import com.jarirahmed.projects.storefront.repository.ProductRepository;
import com.jarirahmed.projects.storefront.repository.StoreUserRepository;
import com.jarirahmed.projects.storefront.seed.StorefrontSeedData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/** Explicit JPA setup keeps the Boot-to-EntityManager boundary visible. */
@Configuration
@EnableTransactionManagement
public class StorefrontJpaConfiguration {
    @Bean
    public DataSource storefrontDataSource(
            @Value("${project4.database-name:project4-storefront}") String databaseName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(Category.class.getPackageName());
        factory.setPersistenceUnitName("project4-unit");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.time_zone", "UTC");
        factory.setJpaProperties(properties);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            jakarta.persistence.EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public static PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

    @Bean
    public org.springframework.boot.CommandLineRunner storefrontSeedRunner(
            StorefrontSeedData seedData) {
        return arguments -> seedData.seed();
    }
}
