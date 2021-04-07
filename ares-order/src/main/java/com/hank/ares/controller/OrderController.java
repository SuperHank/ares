package com.hank.ares.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Value("${spring.profiles.active}")
    private String currentEnvironment;

    @GetMapping("hello")
    private String hello() {
        return String.format("request to order-%s", currentEnvironment);
    }
}
