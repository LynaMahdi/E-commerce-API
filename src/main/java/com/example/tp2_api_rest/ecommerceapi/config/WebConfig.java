package com.example.tp2_api_rest.ecommerceapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Autoriser tous les chemins
                .allowedOrigins("http://localhost:5173")  // Autoriser cette origine
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Autoriser ces méthodes HTTP
                .allowedHeaders("*")  // Autoriser tous les headers
                .allowCredentials(true);  // Autoriser les requêtes avec des cookies/tokens
    }
}
