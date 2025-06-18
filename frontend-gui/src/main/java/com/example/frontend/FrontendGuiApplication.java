package com.example.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FrontendGuiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontendGuiApplication.class, args);
    }
}