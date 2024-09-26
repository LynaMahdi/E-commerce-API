package com.example.tp2_api_rest.ecommerceapi.entity;


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
    Integer id;
    private String name;

    @OneToOne()
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;

    private double price;
    private String description;
    private double stock;
    private String image;



}
