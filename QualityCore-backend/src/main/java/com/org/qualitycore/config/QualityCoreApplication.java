package com.org.qualitycore.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.org.qualitycore")
@EnableJpaRepositories(basePackages = "com.org.qualitycore")
@EnableConfigurationProperties(CloudinaryConfig.class)
public class QualityCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualityCoreApplication.class, args);
    }
}
