package com.hank.ares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EntityScan({"com.hank.ares"})
@EnableFeignClients
public class CouponSettlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponSettlementApplication.class, args);
    }
}
