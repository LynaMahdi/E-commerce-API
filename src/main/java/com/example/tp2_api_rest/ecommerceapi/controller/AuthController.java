package com.example.tp2_api_rest.ecommerceapi.controller;


import com.example.tp2_api_rest.ecommerceapi.responses.AuthResponse;
import com.example.tp2_api_rest.ecommerceapi.responses.LoginRequest;
import com.example.tp2_api_rest.ecommerceapi.responses.RegisterRequest;
import com.example.tp2_api_rest.ecommerceapi.jwt.TokenRefreshRequest;
import com.example.tp2_api_rest.ecommerceapi.repository.UserRespository;
import com.example.tp2_api_rest.ecommerceapi.service.AuthService;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRespository userRespository;

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping(value = "profile")
    public ResponseEntity<User> getProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);

    }
    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = authService.allUsers();

        return ResponseEntity.ok(users);
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }



}
