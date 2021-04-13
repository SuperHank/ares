package com.hank.ares;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.hank.ares.mapper")
public class QuartzApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuartzApplication.class, args);
    }
}
