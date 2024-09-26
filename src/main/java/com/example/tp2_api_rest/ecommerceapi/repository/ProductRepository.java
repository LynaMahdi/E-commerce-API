package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}