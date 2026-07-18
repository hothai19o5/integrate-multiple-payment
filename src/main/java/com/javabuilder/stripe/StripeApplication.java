package com.javabuilder.stripe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StripeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StripeApplication.class, args);
    }

}
