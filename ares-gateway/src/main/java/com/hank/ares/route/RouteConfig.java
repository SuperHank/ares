package com.hank.ares.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RouteConfig {

    /**
     * 手动指定路由规则
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator settlementRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(
                r -> r.path("/settlement/**").uri("http://localhost:8083").id("route-settlement")
        ).build();
    }
}
