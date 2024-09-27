package com.example.tp2_api_rest.ecommerceapi.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue
    private Integer category_id;

    private String categoryName;



    @OneToMany(mappedBy = "category", cascade =  CascadeType.ALL )
    @JsonManagedReference // Gérer la sérialisation de la liste des produits
    private List<Product> products;

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }
}