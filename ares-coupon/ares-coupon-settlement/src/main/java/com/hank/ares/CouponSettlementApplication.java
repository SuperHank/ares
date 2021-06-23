package com.hank.ares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CouponSettlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponSettlementApplication.class, args);
    }
}
