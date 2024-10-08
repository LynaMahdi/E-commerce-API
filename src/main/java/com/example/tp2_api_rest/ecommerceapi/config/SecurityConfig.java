package com.example.tp2_api_rest.ecommerceapi.config;

import com.example.tp2_api_rest.ecommerceapi.jwt.JwtAuthenticationFilter;

import io.jsonwebtoken.lang.Arrays;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
 
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
 

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;
   
   
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequest -> authRequest
                        // Autoriser l'accès non authentifié pour login et register
                        .requestMatchers("/api/authentification/login", "/api/authentification/register", "/api/authentification/refresh-token","api/categories/all","api/product/all").permitAll()

                        // Restreindre les endpoints aux utilisateurs authentifiés avec rôle ADMIN
                        .requestMatchers("/api/authentification/allUsers", "/api/authentification/update/**","api/categories/addCategory","api/categories/update/**","api/categories/delete/**","api/product/admin/addTocategories/**","api/product/update/**").hasRole("ADMIN")

                        // Autoriser tous les utilisateurs authentifiés pour certains endpoints
                        .requestMatchers("/api/authentification/myprofile", "/api/cart/**",   "/api/orders", "/api/orders/**",  "/api/authentification/update/**","/api/payment/secure/**").authenticated()
                )
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors();

                return http.build();
    }

   @Bean
    public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    
    config.setAllowCredentials(true);  // Autoriser les requêtes avec les cookies ou les tokens
    
    // Définir les origines autorisées
    config.setAllowedOrigins(List.of("http://localhost:5173/"));
    
    // Définir les en-têtes autorisés
    config.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization"));
    
    // Définir les méthodes HTTP autorisées
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    
    source.registerCorsConfiguration("/**", config);  // Appliquer cette configuration à tous les chemins
    
    return new CorsFilter(source);  // Retourner un nouveau CorsFilter avec cette configuration
}

}
