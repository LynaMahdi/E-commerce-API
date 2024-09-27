package com.example.tp2_api_rest.ecommerceapi.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product")
public class Product {


    @Id
    @GeneratedValue
    @Column(name = "product_id",nullable = false)  // Spécifie que le nom de la colonne est 'product_id'
    private Integer product_id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference // Éviter la sérialisation de la catégorie dans les produits
    private Category category;

    private double price;
    private String description;
    private double stock;

    private String image;



}
