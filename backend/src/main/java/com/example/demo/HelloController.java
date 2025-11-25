package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Hello from GitHub Actions Local Testing!";
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from API!";
    }
}
