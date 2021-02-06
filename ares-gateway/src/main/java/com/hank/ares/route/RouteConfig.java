package com.hank.ares.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RouteConfig {

    @Bean
    public RouteLocator settlementRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(
                r -> r.path("/settlement/**").uri("http://localhost:8083").id("route-settlement")
        ).build();
    }
}
