package com.example.tp2_api_rest.ecommerceapi.config;

import com.example.tp2_api_rest.ecommerceapi.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf ->
                csrf
                .disable())
            .authorizeHttpRequests(authRequest -> authRequest
                    // Autoriser l'accès non authentifié pour login et register
                    .requestMatchers("/auth/login", "/auth/register","/auth/refresh-token").permitAll()
                    // Protéger les endpoints nécessitant une authentification
                    .requestMatchers("/auth/profile","/users/me", "/api/product","/api/product/**","/api/orders","/api/orders/**","/api/categories/**","/api/categories/**","/api/**","/api/admin/categories/","/auth/update/**","/auth/all").authenticated()
            )
            .sessionManagement(sessionManager ->
               sessionManager
                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

}
