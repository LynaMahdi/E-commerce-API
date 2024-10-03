package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.Category;
import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);
    List<Product> findByCategory(Category category);

    Product findByNameAndDescriptionAndCategory(String name, String description, Category category);
}