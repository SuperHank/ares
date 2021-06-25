package com.hank.ares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.hank.ares.dao")
@EntityScan("com.hank.ares.model")
public class CouponTemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateApplication.class, args);
    }
}
