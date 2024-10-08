package com.example.tp2_api_rest.ecommerceapi.controller;


import com.example.tp2_api_rest.ecommerceapi.entity.Order;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.responses.AuthResponse;
import com.example.tp2_api_rest.ecommerceapi.responses.LoginRequest;
import com.example.tp2_api_rest.ecommerceapi.responses.RegisterRequest;
import com.example.tp2_api_rest.ecommerceapi.jwt.TokenRefreshRequest;
import com.example.tp2_api_rest.ecommerceapi.repository.UserRepository;
import com.example.tp2_api_rest.ecommerceapi.responses.UserProfile;
import com.example.tp2_api_rest.ecommerceapi.service.AuthService;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authentification")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRespository;

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @CrossOrigin
    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }



    @GetMapping("/myprofile")
    public ResponseEntity<UserProfile> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();

            // Mapper l'utilisateur actuel vers le DTO
            UserProfile profileDTO = authService.mapToUserProfileDTO(currentUser);

            return ResponseEntity.ok(profileDTO);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfile>> allUsers() {
        List<UserProfile> users = authService.allUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody RegisterRequest request) throws NotFoundException {
        User updatedUser = authService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser); // Return the updated user
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }



}
