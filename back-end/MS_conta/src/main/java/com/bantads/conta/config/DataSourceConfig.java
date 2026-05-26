package com.bantads.conta.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Configuration
    @EnableJpaRepositories(
        basePackages = "com.bantads.conta.repository.write",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        repositoryFactoryBeanClass = org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean.class
    )
    public static class WriteConfig {
        
        @Primary
        @Bean(name = "entityManagerFactory")
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(
                @Qualifier("writeDataSource") DataSource dataSource) {
            
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource);
            em.setPackagesToScan("com.bantads.conta.model");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            em.setPersistenceUnitName("write");

            Map<String, Object> properties = new HashMap<>();
            properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl");
            properties.put("hibernate.hbm2ddl.auto", "update");
            em.setJpaPropertyMap(properties);
            return em;
        }

        @Primary
        @Bean(name = "transactionManager")
        public PlatformTransactionManager transactionManager(
                @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
            return new JpaTransactionManager(factory.getObject());
        }
    }

    @Configuration
    @EnableJpaRepositories(
        basePackages = "com.bantads.conta.repository.read",
        entityManagerFactoryRef = "readEntityManagerFactory",
        transactionManagerRef = "readTransactionManager"
    )
    public static class ReadConfig {

        @Bean(name = "readEntityManagerFactory")
        public LocalContainerEntityManagerFactoryBean readEntityManagerFactory(
                @Qualifier("readDataSource") DataSource dataSource) {
            
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource);
            em.setPackagesToScan("com.bantads.conta.model");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            em.setPersistenceUnitName("read");

            Map<String, Object> properties = new HashMap<>();
            properties.put("hibernate.integration.envers.enabled", "false"); 
            properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl");
            em.setJpaPropertyMap(properties);

            return em;
        }

        @Bean(name = "readTransactionManager")
        public PlatformTransactionManager readTransactionManager(
                @Qualifier("readEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
            return new JpaTransactionManager(factory.getObject());
        }
    }
}