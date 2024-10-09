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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequest -> authRequest
                        // Autoriser l'accès non authentifié pour login et register
                        .requestMatchers("/api/authentification/login", "/api/authentification/register", "/api/authentification/refresh-token","api/categories/all","api/product/all").permitAll()

                        // Restreindre les endpoints aux utilisateurs authentifiés avec rôle ADMIN
                        .requestMatchers("/api/authentification/allUsers","/api/authentification/delete/**", "/api/authentification/update/**","api/categories/addCategory","api/categories/update/**","api/categories/delete/**","api/product/admin/addTocategories/**","api/product/update/**","/api/reviews/delete/**").hasRole("ADMIN")

                        // Autoriser tous les utilisateurs authentifiés pour certains endpoints
                        .requestMatchers("/api/authentification/myprofile","/api/authentification/logout", "/api/cart/**",    "/api/orders/**",  "/api/authentification/update/**","/api/payment/secure/**","/api/reviews/product/**").authenticated()
                )
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
