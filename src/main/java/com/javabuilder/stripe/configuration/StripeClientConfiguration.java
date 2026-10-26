package com.javabuilder.stripe.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stripe.StripeClient;


@Configuration 
public class StripeClientConfiguration {
    @Value("$stripe.api-key")
    String apiKey;

    @Bean 
    StripeClient stripeClient() {
        return StripeClient.builder()
            .setApiKey(apiKey)
            .build();
    }
}
