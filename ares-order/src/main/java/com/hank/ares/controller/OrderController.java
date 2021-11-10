package com.hank.ares.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Value("${spring.profiles.active}")
    private String currentEnvironment;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("hello")
    private String hello() {
        return String.format("request to order-%s", currentEnvironment);
    }

    @GetMapping("index")
    private String index() {
        return String.format("order-%s index", currentEnvironment);
    }
}
