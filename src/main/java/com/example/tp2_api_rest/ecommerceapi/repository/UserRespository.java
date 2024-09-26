package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRespository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
