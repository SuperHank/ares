package com.hank.ares.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("settlement")
public class TestController {

    @GetMapping("hello")
    private String hello() {
        return "settlement";
    }
}
