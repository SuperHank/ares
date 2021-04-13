package com.hank.ares.controller;

import com.hank.ares.enums.RabbitExchangeEnum;
import com.hank.ares.enums.RabbitQueueEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Value("${spring.profiles.active}")
    private String currentEnvironment;


    @Autowired
    private RestTemplate restTemplate;
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

    @GetMapping("settlementHello")
    private String settlementhello() {
        rabbitTemplate.convertAndSend(RabbitExchangeEnum.TEST.getExchangeName(), RabbitQueueEnum.TEST.getQueueKey(), "hank001");
        return restTemplate.getForObject("http://ARES-SETTLEMENT/settlement/settlement/hello", String.class);
    }
}
