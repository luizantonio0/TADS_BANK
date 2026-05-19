package com.bantads.gerente.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableEnversRepositories
@EnableJpaRepositories(
    basePackages = "com.bantads.gerente.repository",
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class
)
public class JpaConfig {
    
}
