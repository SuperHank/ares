package com.hank.ares.config;

import com.hank.ares.filter.CustomGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(r -> r
                .path("/order/**")
                .uri("lb://order-service")
                .filters(new CustomGatewayFilter())
                .id("order-service-custom")).build();
    }
}