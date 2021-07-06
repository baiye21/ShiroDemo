package com.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/hello")
    public String hello() {
        System.out.println("TEST DEMO");
        return "Hello World Spring Boot!";
    }
}
