package com.farmer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String root() {
        return "App is alive";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
