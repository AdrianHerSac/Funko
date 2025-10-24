package dev.adrian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FunkoApplication {

    static void main(String[] args) {
        SpringApplication.run(FunkoApplication.class, args);
    }
}