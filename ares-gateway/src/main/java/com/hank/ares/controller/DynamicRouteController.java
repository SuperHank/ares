package com.hank.ares.controller;

import com.hank.ares.service.impl.DynamicRouteServiceImpl;
import io.swagger.annotations.ApiModel;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@ApiModel("路由配置")
@RestController
@RequestMapping("/route")
public class DynamicRouteController {

    @Resource
    private DynamicRouteServiceImpl dynamicRouteService;

    @PostMapping("/add")
    public String create(@RequestBody RouteDefinition entity) {
        int result = dynamicRouteService.add(entity);
        return String.valueOf(result);
    }

    @PostMapping("/update")
    public String update(@RequestBody RouteDefinition entity) {
        int result = dynamicRouteService.update(entity);
        return String.valueOf(result);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable String id) {
        return dynamicRouteService.delete(id);
    }
}
