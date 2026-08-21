package com.ticketing.main_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${pricing.service.url:http://localhost:8081}")
    private String pricingServiceUrl;

    @Bean
    public RestClient pricingRestClient() {
        return RestClient.builder()
                .baseUrl(pricingServiceUrl)
                .build();
    }
}