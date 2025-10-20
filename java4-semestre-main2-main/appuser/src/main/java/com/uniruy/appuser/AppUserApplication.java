package com.uniruy.appuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AppUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppUserApplication.class, args);
    }
}